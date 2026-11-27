package cl.duoc.ferreteria.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;

public final class Rut {
    private Rut() {}

    public static String normalizar(String valor) {
        String rut = valor == null ? "" : valor.replaceAll("[.\\s-]", "").toUpperCase(Locale.ROOT);
        if (!rut.matches("[0-9]{7,8}[0-9K]")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Revisa el RUT y su dígito verificador");
        }
        int suma = 0, factor = 2;
        for (int i = rut.length() - 2; i >= 0; i--) {
            suma += Character.digit(rut.charAt(i), 10) * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int valorDigito = 11 - suma % 11;
        String digito = valorDigito == 11 ? "0" : valorDigito == 10 ? "K" : String.valueOf(valorDigito);
        if (!rut.endsWith(digito)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Revisa el RUT y su dígito verificador");
        }
        return rut.substring(0, rut.length() - 1) + "-" + digito;
    }
}
