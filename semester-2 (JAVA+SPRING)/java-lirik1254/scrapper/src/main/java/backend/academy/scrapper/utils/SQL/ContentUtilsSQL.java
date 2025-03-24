package backend.academy.scrapper.utils.SQL;

import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.entities.SQL.Content;
import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.entities.SQL.Users;
import backend.academy.scrapper.repositories.SQL.ContentRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UrlRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import dto.ContentDTO;
import dto.UpdateType;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@Slf4j
public class ContentUtilsSQL {
    private final UpdateLinkClient updateLinkClient;
    private final UrlRepositorySQL urlRepositorySQL;
    private final ContentRepositorySQL contentRepositorySQL;
    private final UsersRepositorySQL usersRepositorySQL;

    public List<ContentDTO> fromContentListToContentDTOList(List<Content> contents) {
        List<ContentDTO> contentDTOS = new ArrayList<>();

        contents.forEach(content -> {
            contentDTOS.add(new ContentDTO(
                    UpdateType.valueOf(content.updatedType()),
                    content.title(),
                    content.userName(),
                    content.creationTime(),
                    content.answer()));
        });

        return contentDTOS;
    }

    @Transactional
    public void updateContentAndSend(Link link, List<ContentDTO> newContent, List<ContentDTO> newItems) {
        Url url = urlRepositorySQL.getByUrlId(link.urlId());
        if (!newItems.isEmpty()) {
            contentRepositorySQL.deleteContentByUrlId(url.urlId());
            newContent.forEach(content -> {
                log.atInfo()
                        .addKeyValue("url", url.url())
                        .addKeyValue("access-type", "SQL")
                        .setMessage("Добавление контента в URL")
                        .log();
                contentRepositorySQL.addContent(
                        content.type().name(),
                        content.answer(),
                        content.creationTime(),
                        content.title(),
                        content.userName(),
                        url.urlId());
            });
        }

        Users users = usersRepositorySQL.getByUsersId(link.usersId());

        newItems.forEach(content -> {
            Long chatId = users.chatId();
            String sendUrl = url.url();
            log.atInfo()
                    .addKeyValue("chatId", chatId)
                    .addKeyValue("url", sendUrl)
                    .addKeyValue("access-type", "ORM")
                    .setMessage("Отправка контента")
                    .log();
            updateLinkClient.sendUpdate(chatId, sendUrl, content);
        });
    }
}
