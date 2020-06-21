package ru.ecache.service.jpa;

import lombok.AllArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import ru.ecache.service.ECacheService;
import ru.ecache.service.EEntitiesCacheStore;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@AllArgsConstructor
public class ECacheHibernateEventListener
        implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private final EntityManagerFactory entityManagerFactory;
    private final EEntitiesCacheStore entitiesCacheStore;
    private final ECacheService<String, Long> cacheStoreService;

    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(this);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {

        if (event != null && entitiesCacheStore.isCacheableEntity(event.getEntity().getClass())) {
            cacheStoreService.put(event.getEntity().getClass().getName(), System.currentTimeMillis());
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {

        if (event != null && entitiesCacheStore.isCacheableEntity(event.getEntity().getClass())) {
            cacheStoreService.put(event.getEntity().getClass().getName(), System.currentTimeMillis());
        }
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {

        if (event != null && entitiesCacheStore.isCacheableEntity(event.getEntity().getClass())) {
            cacheStoreService.put(event.getEntity().getClass().getName(), System.currentTimeMillis());
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return true;
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
