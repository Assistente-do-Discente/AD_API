package br.assistentediscente.api.main.dto;

public record LoginDTO(
        String username,
        String password,
        String institutionName) {
}
