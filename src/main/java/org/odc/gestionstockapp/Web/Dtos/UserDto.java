package org.odc.gestionstockapp.Web.Dtos;

import lombok.Data;
import org.odc.gestionstockapp.Datas.Enums.Role;

@Data
public class UserDto {
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private Role role;

}
