package com.elotech.desafio.service;

import com.elotech.desafio.dto.GoogleBookResponseDTO;
import com.elotech.desafio.dto.LivroRequestDTO;
import com.elotech.desafio.entity.Emprestimo;
import com.elotech.desafio.entity.Livro;
import com.elotech.desafio.entity.Usuario;
import com.elotech.desafio.repository.EmprestimoRepository;
import com.elotech.desafio.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LivroService {

    private final RestClient googleBooksClient;

    @Value("${google.books.api.key}")
    private  String apiKey;

    private final LivroRepository livroRepository;

    private final UsuarioService usuarioService;

    private final EmprestimoRepository emprestimoRepository;

    public Livro criarLivro(LivroRequestDTO requestDTO){

        Livro livroNovo = Livro.builder().titulo(requestDTO.titulo()).autor(requestDTO.autor()).
                isbn(requestDTO.isbn()).categoria(requestDTO.categoria()).dataPublicacao(requestDTO.dataPublicacao()).build();

        return livroRepository.save(livroNovo);
    }

    public Livro retornaLivro(Long id){
        return livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    }

    public List<Livro> retornaLivrosGooglePorTitulo(String titulo){

        GoogleBookResponseDTO responseDTO = chamadaGoogle("intitle:" + titulo);

        if(responseDTO.items() == null || responseDTO.items().isEmpty()){
            return List.of();
        }

        return responseDTO.items().stream().map(item -> Livro.builder()
                .titulo(item.volumeInfo().title())
                .autor(juntaLista(item.volumeInfo().authors()))
                .categoria(juntaLista(item.volumeInfo().categories()))
                .isbn(extraiIsbn(item.volumeInfo().industryIdentifiers()))
                .dataPublicacao(parseDataPublicacao(item.volumeInfo().publishedDate()))
                .build()).toList();
    }

    public GoogleBookResponseDTO retornaLivrosGooglePorIsbn(String isbn){
        return chamadaGoogle("isbn:" + isbn);
    }

    public Livro criaLivroGoogle(String isbn){

        GoogleBookResponseDTO responseDTO = retornaLivrosGooglePorIsbn(isbn);

        if(responseDTO.items() == null || responseDTO.items().isEmpty()){
            throw new RuntimeException("Nenhum livro encontrado");
        }

        GoogleBookResponseDTO.ItemDTO item = responseDTO.items().getFirst();



        return livroRepository.save(Livro.builder()
                .titulo(item.volumeInfo().title())
                .autor(juntaLista(item.volumeInfo().authors()))
                .categoria(juntaLista(item.volumeInfo().categories()))
                .isbn(extraiIsbn(item.volumeInfo().industryIdentifiers()))
                .dataPublicacao(parseDataPublicacao(item.volumeInfo().publishedDate()))
                .build());
    }


    public Livro atualizaLivro(Long id, LivroRequestDTO requestDTO){

        Livro livroEditado = retornaLivro(id);

        livroEditado.setAutor(requestDTO.autor());
        livroEditado.setIsbn(requestDTO.isbn());
        livroEditado.setCategoria(requestDTO.categoria());
        livroEditado.setDataPublicacao(requestDTO.dataPublicacao());
        livroEditado.setTitulo(requestDTO.titulo());

        return livroRepository.save(livroEditado);
    }

    public void deletaLivro(Long id){
        Livro livroDeletado = retornaLivro(id);

        livroRepository.delete(livroDeletado);

    }

    public Page<Livro> listaLivros(Pageable pageable){
        return livroRepository.findAll(pageable);
    }

    public List<Livro> recomendaLivro(Long usuarioId){

        Usuario usuario = usuarioService.retornaUsuario(usuarioId);

        List<Emprestimo> emprestimos = emprestimoRepository.findByUsuario(usuario);

        if(emprestimos == null){return List.of();}

        List<String> categorias = emprestimos.stream().map(i -> i.getLivro().getCategoria())
                .distinct().toList();

        List<Long> idLivrosEmprestados = emprestimos.stream().map(i -> i.getLivro().getId()).toList();

        return livroRepository.findByCategoriaInAndIdNotIn(categorias, idLivrosEmprestados);
    }


    private LocalDate parseDataPublicacao(String dataPublicacao){
        if(dataPublicacao == null) return  null;

        if(dataPublicacao.matches("\\d{4}-\\d{2}-\\d{2}")){
            return LocalDate.parse(dataPublicacao);
        }

        if (dataPublicacao.matches("\\d{4}-\\d{2}")) {
            return LocalDate.parse(dataPublicacao + "-01");
        }

        if (dataPublicacao.matches("\\d{4}")) {
            return LocalDate.parse(dataPublicacao + "-01-01");
        }

        return null;
    }

    private GoogleBookResponseDTO chamadaGoogle(String query){

        return googleBooksClient.get()
                .uri("/volumes?q={query}&key={key}", query, apiKey)
                .retrieve()
                .body(GoogleBookResponseDTO.class);
    }

    private String extraiIsbn(List<GoogleBookResponseDTO.IndustryIdentifierDTO> industryIdentifiers){

        if(industryIdentifiers == null) return "Não informado";

        return industryIdentifiers.stream().filter(i -> i.type().equals("ISBN_13"))
                .findFirst().or(() -> industryIdentifiers.stream().filter(i -> i.type().equals("ISBN_10")).findFirst())
                .map(GoogleBookResponseDTO.IndustryIdentifierDTO::identifier)
                .orElse("Não informado");


    }

    private String juntaLista(List<String> lista){
        if(lista == null || lista.isEmpty()){
            return "Não informado";
        }

        return String.join(", ", lista);
    }
}
