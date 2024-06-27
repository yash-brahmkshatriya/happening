package ln.dev.repository;

import ln.dev.pojo.EventPojo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CustomEventRepository, MongoRepository<EventPojo, String> {

}
