package org.odc.gestionstockapp.Web.Dtos;

import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
