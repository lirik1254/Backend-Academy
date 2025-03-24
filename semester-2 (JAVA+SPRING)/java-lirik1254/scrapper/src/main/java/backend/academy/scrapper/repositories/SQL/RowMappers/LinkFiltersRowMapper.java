package backend.academy.scrapper.repositories.SQL.RowMappers;

import backend.academy.scrapper.entities.SQL.LinkFilters;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class LinkFiltersRowMapper implements RowMapper<LinkFilters> {
    @Override
    public LinkFilters mapRow(ResultSet rs, int rowNum) throws SQLException {
        LinkFilters linkFilters = new LinkFilters();
        linkFilters.linkId(rs.getLong("link_id"));
        linkFilters.filters(rs.getString("filters"));
        return linkFilters;
    }
}
