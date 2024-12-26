package org.odc.gestionstockapp.Web.Dtos;

import lombok.Data;

@Data
public class UserDtoUpdate {
    private String email;
    private String password;
    private String nom;
    private String prenom;
}
