package ru.melowetty.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melowetty.repository.impl.BaseRepositoryImpl;

import java.io.Serializable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class BaseRepositoryImplTest {

    private BaseRepositoryImpl<Entity, Integer> repository;

    @BeforeEach
    void setUp() {
        repository = new BaseRepositoryImpl<>() {
            @Override
            protected Integer getIndexFromEntity(Entity entity) {
                return entity.getId();
            }
        };
    }

    @Test
    public void create_addsEntityToStorage() {
        Entity entity = new Entity(1, "Test Entity");

        Entity result = repository.create(entity);

        assertEquals(entity, result);
        assertTrue(repository.existsById(1));
    }

    @Test
    public void removeById_removesEntityFromStorage() {
        Entity entity = new Entity(1, "Test Entity");
        repository.create(entity);

        repository.removeById(1);

        assertFalse(repository.existsById(1));
    }

    @Test
    public void update_updatesExistingEntity() {
        Entity entity = new Entity(1, "Test Entity");
        repository.create(entity);
        Entity updatedEntity = new Entity(1, "Updated Entity");

        Entity result = repository.update(updatedEntity);

        assertEquals(updatedEntity, result);
        assertEquals("Updated Entity", repository.findById(1).getName());
    }

    @Test
    public void findById_existingId_returnsEntity() {
        Entity entity = new Entity(1, "Test Entity");
        repository.create(entity);

        Entity result = repository.findById(1);

        assertEquals(entity, result);
    }

    @Test
    public void findById_nonExistingId_returnsNull() {
        Entity result = repository.findById(1);

        assertNull(result);
    }

    @Test
    public void findAll_returnsAllEntities() {
        Entity entity1 = new Entity(1, "Test Entity 1");
        Entity entity2 = new Entity(2, "Test Entity 2");
        repository.create(entity1);
        repository.create(entity2);

        List<Entity> result = repository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(entity1));
        assertTrue(result.contains(entity2));
    }

    @Test
    public void existsById_existingId_returnsTrue() {
        Entity entity = new Entity(1, "Test Entity");
        repository.create(entity);

        boolean result = repository.existsById(1);

        assertTrue(result);
    }

    @Test
    public void existsById_nonExistingId_returnsFalse() {
        boolean result = repository.existsById(1);

        assertFalse(result);
    }

    @Test
    public void count_returnsNumberOfEntities() {
        Entity entity1 = new Entity(1, "Test Entity 1");
        Entity entity2 = new Entity(2, "Test Entity 2");
        repository.create(entity1);
        repository.create(entity2);

        int result = repository.count();

        assertEquals(2, result);
    }

    static class Entity implements Serializable {
        private Integer id;
        private String name;

        public Entity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entity entity = (Entity) o;

            if (!id.equals(entity.id)) return false;
            return name.equals(entity.name);
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}