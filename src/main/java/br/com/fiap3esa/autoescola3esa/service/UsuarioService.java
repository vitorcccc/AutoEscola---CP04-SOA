package br.com.fiap3esa.autoescola3esa.service;

import br.com.fiap3esa.autoescola3esa.domain.agenda.ValidacaoException;
import br.com.fiap3esa.autoescola3esa.domain.usuario.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DadosDetalhamentoUsuario cadastrarUsuario(DadosCadastroUsuario dados) {
        if (repository.existsByLogin(dados.login())) {
            throw new ValidacaoException("Já existe um usuário cadastrado com esse login!");
        }
        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        Usuario usuario = new Usuario(dados, senhaCriptografada);
        Usuario saved = repository.save(usuario);
        return new DadosDetalhamentoUsuario(saved);
    }

    public List<DadosDetalhamentoUsuario> listarUsuarios() {
        return repository.findAll()
                .stream()
                .map(DadosDetalhamentoUsuario::new)
                .toList();
    }

    @Transactional
    public DadosDetalhamentoUsuario atualizarPerfilUsuario(DadosAtualizacaoPerfilUsuario dados) {
        Usuario usuario = repository.findById(dados.id())
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));
        usuario.atualizarPerfil(dados.perfil());
        Usuario saved = repository.save(usuario);
        return new DadosDetalhamentoUsuario(saved);
    }

    @Transactional
    public void excluirUsuario(Long id) {
        if (!repository.existsById(id)) {
            throw new UsuarioNotFoundException("ID do usuário informado não existe!");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void alterarSenha(String loginAutenticado, DadosAlterarSenha dados) {
        Usuario usuario = repository.findUsuarioByLogin(loginAutenticado);
        if (usuario == null) {
            throw new UsuarioNotFoundException("Usuário autenticado não encontrado!");
        }
        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getPassword())) {
            throw new ValidacaoException("Senha atual informada está incorreta!");
        }
        String novaSenhaCriptografada = passwordEncoder.encode(dados.senhaNova());
        usuario.atualizarSenha(novaSenhaCriptografada);
        repository.save(usuario);
    }
}
