package sn.esp.gestionUtilisateur.utility;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;

import jakarta.servlet.http.HttpServletRequest;
import sn.esp.gestionUtilisateur.entities.UserPrincipal;

import static sn.esp.gestionUtilisateur.constant.SecurityConstant.GET_ARRAYS_LLC;
import static sn.esp.gestionUtilisateur.constant.SecurityConstant.GET_ARRAYS_ADMINISTRATION;
import static sn.esp.gestionUtilisateur.constant.SecurityConstant.AUTHORITIES;
import static sn.esp.gestionUtilisateur.constant.SecurityConstant.EXPIRATION_TIME;
import static sn.esp.gestionUtilisateur.constant.SecurityConstant.TOKEN_CANNOT_BE_VERIFIED;

@Component
public class JWTTokenProvider {

//	@Value("${jwt.secret}") // fichier yml
//	private String secret;
	
	
	private String secret = "[a-zA-Z0-9._]^+$Guidelines89797987forAlphabeticalArraNumeralsandOtherSymbo$";
	

	public String generateJwtToken(UserPrincipal userPrincipal) {

		// Récupère les revendications à partir du UserPrincipal
		String[] claims = getClaimsFromUser(userPrincipal);

		// Crée un nouveau JWT avec les informations d'émetteur et d'audience, la date d'émission, le sujet, les revendications sous forme de tableau et la date d'expiration
		return JWT.create()
				.withIssuer(GET_ARRAYS_LLC) // Émetteur du JWT
				.withAudience(GET_ARRAYS_ADMINISTRATION) // Audience du JWT
				.withIssuedAt(new Date()) // Date d'émission du JWT
				.withSubject(userPrincipal.getUsername()) // Sujet du JWT
				.withArrayClaim(AUTHORITIES, claims) // Revendications sous forme de tableau du JWT
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Date d'expiration du JWT
				.sign(HMAC512(secret.getBytes())); // Signe le JWT en utilisant la clé secrète et l'algorithme HMAC 512
	}

	private String[] getClaimsFromUser(UserPrincipal user) {

		List<String> authorities = new ArrayList<>();

		for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
			authorities.add(grantedAuthority.getAuthority());
		}

		return authorities.toArray(new String[0]); // sous forme de tableau de chaine
	}


	public List<GrantedAuthority> getAuthorities(String token) {

	    // Récupère les revendications à partir du token
	    String[] claims = getClaimsFromToken(token);
	    
	    // Map chaque revendication en SimpleGrantedAuthority, 
	    // puis collecte le tout dans une liste et la renvoie
	    return stream(claims)
	            .map(SimpleGrantedAuthority::new)
	            .collect(Collectors.toList());
	}


	private String[] getClaimsFromToken(String token) {
		
		JWTVerifier verifier = getJWTVerifier();
		
		return verifier
				.verify(token)
				.getClaim(AUTHORITIES)
				.asArray(String.class);
	}
	
	private JWTVerifier getJWTVerifier() {
		
		JWTVerifier verifier;
		
		try {
			Algorithm algorithm = HMAC512(secret);
			
			verifier = JWT.require(algorithm).withIssuer(GET_ARRAYS_LLC).build();
			
		}catch (JWTVerificationException exception) {
			
			throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
		}
		
		return verifier;
	}
	

	public Authentication getAuthentication(
							String username, 
							List<GrantedAuthority> authorities, 
							HttpServletRequest request) {
		
		UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
		
		userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		
		return userPasswordAuthToken;
	}
	
	public boolean isTokenValid(String username, String token) {
		
        JWTVerifier verifier = getJWTVerifier();
        
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }	

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
    	
        Date expiration = verifier
        					.verify(token)
        					.getExpiresAt();
        
        return expiration.before(new Date());
    }


	public String getSubject(String token) {
		
		JWTVerifier verifier = getJWTVerifier();
		
		return verifier
				.verify(token)
				.getSubject();
	}
	

}
