package scaler.com.userservices.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAlreadyExistException extends Exception{
    private String message;
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
