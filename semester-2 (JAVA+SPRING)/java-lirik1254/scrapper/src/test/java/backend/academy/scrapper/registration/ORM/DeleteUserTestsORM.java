package backend.academy.scrapper.registration.ORM;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.registration.DeleteUsersTestsBase;
import backend.academy.scrapper.repositories.ORM.ContentRepositoryORM;
import backend.academy.scrapper.repositories.ORM.LinkRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.UpdateType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteUserTestsORM extends DeleteUsersTestsBase {
    @Autowired
    private LinkRepositoryORM linkRepositoryORM;

    @Autowired
    private ContentRepositoryORM contentRepositoryORM;

    @Autowired
    private UrlRepositoryORM urlRepositoryORM;

    @Autowired
    private UsersRepositoryORM usersRepositoryORM;

    @Test
    @DisplayName("Пользователь, которого собираются удалить, существует")
    public void test1() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        when(gitHubInfoClient.getGithubContent("https://github.com/lirik1254/abTestRepo"))
                .thenReturn(List.of(new ContentDTO(
                        contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer)));

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        assertTrue(usersRepositoryORM.existsByChatId(123L));
        assertFalse(linkRepositoryORM.findAll().isEmpty());
        assertFalse(urlRepositoryORM.findAll().isEmpty());
        assertFalse(contentRepositoryORM.findAll().isEmpty());

        performDeleteUserRequest(123L);

        assertTrue(usersRepositoryORM.findAll().isEmpty());
        assertTrue(linkRepositoryORM.findAll().isEmpty());
        assertTrue(urlRepositoryORM.findAll().isEmpty());
        assertTrue(contentRepositoryORM.findAll().isEmpty());
    }

    @Test
    @DisplayName("Пользователь, которого собираются удалить, не существует")
    public void test2() throws Exception {
        assertTrue(usersRepositoryORM.findAll().isEmpty());

        performDeleteUserRequestNotFound(52L);
    }
}
