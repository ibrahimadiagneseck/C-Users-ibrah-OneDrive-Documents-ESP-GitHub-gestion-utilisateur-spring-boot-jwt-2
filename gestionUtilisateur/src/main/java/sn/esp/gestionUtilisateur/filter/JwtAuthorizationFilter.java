package sn.esp.gestionUtilisateur.filter;

import static sn.esp.gestionUtilisateur.constant.SecurityConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.esp.gestionUtilisateur.utility.JWTTokenProvider;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private JWTTokenProvider jwtTokenProvider;
	
	public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// Vérifier si la méthode HTTP est OPTIONS
		if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
		    // Si oui, définir le code de réponse HTTP sur OK (200)
		    response.setStatus(OK.value());
		} else {
		    // Si non, vérifier si le header Authorization est présent
		    String authorizationHeader = request.getHeader(AUTHORIZATION);
		    
		    // Si le header Authorization n'est pas présent ou ne commence pas par "Bearer ", continuer la chaîne de filtres
		    if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
		        filterChain.doFilter(request, response);
		        return;
		    }
		    
		    // Si le header Authorization est présent et commence par "Bearer ", extraire le token JWT
		    String token = authorizationHeader.substring(TOKEN_PREFIX.length()); // supprimer l'en-tete
		    String username = jwtTokenProvider.getSubject(token);
		    
		    // Vérifier si le token est valide et que l'authentification n'a pas déjà été effectuée
		    if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
		        // Si oui, récupérer les autorisations et l'authentification associée au token JWT
		        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
		        Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
		        // Définir l'authentification dans le contexte de sécurité
		        SecurityContextHolder.getContext().setAuthentication(authentication); // pour les sessions
		    } else {
		        // Si le token n'est pas valide ou que l'authentification a déjà été effectuée, effacer le contexte de sécurité
		        SecurityContextHolder.clearContext();
		    }
		}
		// Continuer la chaîne de filtres
		filterChain.doFilter(request, response);

		
	}

}
