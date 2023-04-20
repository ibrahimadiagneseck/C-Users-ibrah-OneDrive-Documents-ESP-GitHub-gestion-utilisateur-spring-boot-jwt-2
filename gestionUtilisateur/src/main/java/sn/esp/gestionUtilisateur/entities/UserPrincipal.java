package sn.esp.gestionUtilisateur.entities;

import static java.util.Arrays.stream;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }
    
    /*
     * La méthode getAuthorities() 
     * retourne une collection d'objets GrantedAuthority, 
     * qui représentent les permissions ou rôles de l'utilisateur. 
     * L'implémentation utilise la méthode stream() 
     * pour convertir un tableau d'autorisations en une 
     * liste d'objets SimpleGrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(this.user.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }
}
