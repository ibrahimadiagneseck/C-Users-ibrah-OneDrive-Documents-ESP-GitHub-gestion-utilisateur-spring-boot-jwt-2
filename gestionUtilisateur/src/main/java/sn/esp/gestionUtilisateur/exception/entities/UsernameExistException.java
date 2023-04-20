package sn.esp.gestionUtilisateur.exception.entities;

public class UsernameExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public UsernameExistException(String message) {
        super(message);
    }
}
