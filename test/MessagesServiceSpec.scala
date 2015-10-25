import java.util.Date

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import play.api.test.PlaySpecification
import ru.org.codingteam.cttalk.models.{MessageReceiver, Message}
import ru.org.codingteam.cttalk.services.MessagesServiceImpl

/**
 * Created by hgn on 25.10.2015.
 */
class MessagesServiceSpec extends PlaySpecification with Mockito {
  "MessageService.send" should {
    "-- succeed when sending to existing recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      service.register("existing", mock[MessageReceiver])
      service.send("existing", Message("sender", new Date(), "message")) map {
        result => result must be equalTo true
      } await
    }

    "-- fail when sending to unknown recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      service.send("unknown", Message("sender", new Date(), "message")) map {
        result => result must be equalTo false
      } await
    }
  }

  "MessageService.get" should {
    "-- receive previously sent messages" in {
    }

    "-- return empty sequence if there is no messages" in {
    }

    "-- fail if trying to get messages for unknown recipient" in {
    }
  }

  "MessageService.register" should {
    "-- register recipient if not previously registered" in {}
    "-- fail if trying to register an already registered recipient" in {}
  }
}
