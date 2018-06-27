package net.juanlopes.ettree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IntQueueTest {
    @Test
    public void testAddAndRemove() throws Exception {
        IntQueue q = new IntQueue();
        for (int i = 0; i < 30; i++)
            q.push(i);
        for (int i = 0; i < 30; i++)
            assertThat(q.pop()).isEqualTo(i);
    }

    @Test
    public void testClear() throws Exception {
        IntQueue q = new IntQueue();
        for (int i = 0; i < 10; i++)
            q.push(i);
        q.clear();
        assertThat(q.isEmpty()).isTrue();
    }
}