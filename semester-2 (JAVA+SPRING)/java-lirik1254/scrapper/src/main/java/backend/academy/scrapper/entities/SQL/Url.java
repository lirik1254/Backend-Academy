package backend.academy.scrapper.entities.SQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Url {
    private Long id;
    private String url;
    private String linkType;
}
