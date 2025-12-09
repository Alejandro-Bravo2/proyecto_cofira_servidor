package daw2a.gestionbiblioteca.dto.avatar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AvatarDTO {
    private MultipartFile file;


}