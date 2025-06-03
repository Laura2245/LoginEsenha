package sistemalogin.login ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    // Página inicial - redireciona para login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    // Exibir página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    // Processar login
    @PostMapping("/login")
    public String processarLogin(@RequestParam String email, 
                               @RequestParam String senha, 
                               HttpSession session, 
                               Model model) {
        
        Usuario usuario = usuarioService.autenticar(email, senha);
        
        if (usuario != null) {
            session.setAttribute("usuarioLogado", usuario);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("erro", "Email ou senha inválidos!");
            return "login";
        }
    }
    
    // Exibir página de cadastro
    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }
    
    // Processar cadastro
    @PostMapping("/cadastro")
    public String processarCadastro(@RequestParam String nome,
                                  @RequestParam String email,
                                  @RequestParam String senha,
                                  Model model) {
        
        if (usuarioService.emailJaExiste(email)) {
            model.addAttribute("erro", "Email já cadastrado!");
            return "cadastro";
        }
        
        Usuario novoUsuario = new Usuario(email, senha, nome);
        usuarioService.salvarUsuario(novoUsuario);
        
        model.addAttribute("sucesso", "Usuário cadastrado com sucesso!");
        return "login";
    }
    
    // Dashboard (área logada)
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}