package com.ya0ne.web.security;

import java.util.List;

import org.apache.log4j.Logger;
import org.owasp.esapi.AccessReferenceMap;
import org.owasp.esapi.errors.AccessControlException;
import org.owasp.esapi.reference.RandomAccessReferenceMap;

import com.ya0ne.core.domain.abstracts.Entity;

public final class EntityAccessMap<T extends Entity<Long>> implements AccessMap<T> {
	private static Logger logger = Logger.getLogger(EntityAccessMap.class);

    private static final long serialVersionUID = -436098952674898327L;
    private AccessReferenceMap<String> map = new RandomAccessReferenceMap();

    private EntityAccessMap(final List<T> entities) {
    	logger.info("Entities size: " + entities.size());
        for (final T entity : entities) {
            map.addDirectReference(entity);
        }
    }

	public static <T extends Entity<Long>> AccessMap<T> create(final List<T> entities) {
    	logger.info("calling create with entities' size " + entities.size() );
    	for( int i = 0; i<entities.size(); i++ ) {
    	    logger.info("map entity: " + entities.get(i) + "->" + (entities.get(i)).getId());
    	}
        return new EntityAccessMap<>(entities);
    }

    public String getIndirectReference(final T entity) {
    	logger.info("Entity: " + map.getIndirectReference(entity));
        return map.getIndirectReference(entity);
    }

    public T getDirectReference(final String indirectReference) {
        try {
            return map.getDirectReference(indirectReference);
        } catch (AccessControlException e) {
            throw new IllegalArgumentException("Indirect Reference is not valid", e);
        }
    }
}
