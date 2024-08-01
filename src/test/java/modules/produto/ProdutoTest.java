package modules.produto;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.UsuarioPojo;

import static dataFactory.ProdutoDataFactory.criarUmProdutoComumComValorIgualA;
import static dataFactory.UsuarioDataFactory.loginUsuario;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Testes de API Rest do modulo de Produto")
public class ProdutoTest {
    private String token;

    @BeforeEach
    public void beforeEach(){
        //Configurando os dados de API rest da lojinha
        baseURI = "http://165.227.93.41";
        basePath = "/lojinha";

        //Obter o token do usuario
        this.token = given()
                .contentType(ContentType.JSON)
                .body(loginUsuario("admin","admin"))
                .when()
                .post("/v2/login")
                .then()
                .extract()
                .path("data.token");

    }

    @Test
    @DisplayName("Validar que o valor do produto igual a 0.0 nao e valido")
    public void testValidarLimitesProibidosValorProdutoZerado(){
        //Tentar inserir um produto com valor 0.0 para validar mensagem de erro
        //status code 422
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(criarUmProdutoComumComValorIgualA(0))
        .when()
            .log().all()
                .post("/v2/produtos")
        .then()
            .log().all()
                .assertThat()
                .body("error",equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    @DisplayName("Validar que o valor do produto maior que 7000 nao e valido")
    public void testValidarLimitesProibidosValorProdutoMaiorQue7Mil(){
        //Tentar inserir um produto com valor 0.0 para validar mensagem de erro
        //status code 422
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(criarUmProdutoComumComValorIgualA(7000.01))
                .when()
                .log().all()
                .post("/v2/produtos")
                .then()
                .log().all()
                .assertThat()
                .body("error",equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }
}
