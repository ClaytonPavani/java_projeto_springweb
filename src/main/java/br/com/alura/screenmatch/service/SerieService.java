package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Criamos essa anotação @Service para que o spring entenda que é uma classe que ele irá gerenciar
// Geralmente é uma Classe que contém as regras de negócios do nosso serviço
@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    // Aqui criamos um método que vamos aplicar dentro de outros métodos acima
    private List<SerieDTO> converteDados(List<Serie> series){
                 return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDados(repositorio.lancamentosMaisRecentes());
    }

    public SerieDTO obterPorId(Long id) {
       // Aqui usamos um método diferente, Find By Id - Como é um Optional, temos que tratar e converter para Serie DTO
       Optional<Serie> serie = repositorio.findById(id);

       if (serie.isPresent()) {
           // Temos que declarar o "S" para pegar o método GET
           Serie s = serie.get();
           return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
       }
       return null;
    }

    // Criamos um método para nós exibirmos todas os episódios de cada temporada
    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = repositorio.findById(id);

        if (serie.isPresent()) {
            // Temos que declarar o "S" para pegar o método GET
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
        return repositorio.obterEpisodiosPorTemporada(id, numero)
                .stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterSeriesPorCategoria(String nomeGenero) {
        // Vamos fazer uma conversão do endereço dinâmico da URL e vamos declarar:
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        // Agora vamos chamar o método do nosso repositório, passando a categoria que criamos e vamos chamar um conversor:
        return converteDados(repositorio.findByGenero(categoria));
    }
}

