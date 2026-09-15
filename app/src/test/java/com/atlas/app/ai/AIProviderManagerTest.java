package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.*;

public class AIProviderManagerTest {

    private static class TestCloudProvider
            extends CloudAIProvider {

        @Override
        public String getName() {
            return "test-cloud";
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public AIResponse generate(
                AIRequest request
        ) {
            return AIResponse.success("cloud");
        }
    }

    private static class TestLocalProvider
            extends LocalAIProvider {

        @Override
        public String getName() {
            return "test-local";
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public AIResponse generate(
                AIRequest request
        ) {
            return AIResponse.success("local");
        }
    }

    @Test
    public void selectsPreferredProviderType() {
        AIProviderManager manager =
                new AIProviderManager();

        TestCloudProvider cloud =
                new TestCloudProvider();

        TestLocalProvider local =
                new TestLocalProvider();

        manager.register(cloud);
        manager.register(local);

        manager.setPreferredType(
                AIProviderType.LOCAL
        );

        assertSame(
                local,
                manager.getProvider()
        );
    }

    @Test
    public void selectorReturnsProvider() {
        AIProviderManager manager =
                new AIProviderManager();

        TestCloudProvider cloud =
                new TestCloudProvider();

        manager.register(cloud);

        ProviderSelection selection =
                new AIProviderSelector(manager)
                        .select(
                                AIProviderType.CLOUD
                        );

        assertTrue(
                selection.isSuccess()
        );

        assertSame(
                cloud,
                selection.getProvider()
        );
    }

    @Test
    public void selectorReportsUnavailableProvider() {
        AIProviderManager manager =
                new AIProviderManager();

        ProviderSelection selection =
                new AIProviderSelector(manager)
                        .select(
                                AIProviderType.CLOUD
                        );

        assertFalse(
                selection.isSuccess()
        );

        assertNotNull(
                selection.getReason()
        );
    }
}
