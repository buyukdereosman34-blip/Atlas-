package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AIProviderRouterTest {

    private AIProvider cloudProvider() {
        return new AIProvider() {
            @Override
            public String getName() {
                return "cloud-test";
            }

            @Override
            public AIProviderType getType() {
                return AIProviderType.CLOUD;
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public AIResponse generate(AIRequest request) {
                return AIResponse.success("cloud");
            }
        };
    }

    private AIProvider localProvider() {
        return new AIProvider() {
            @Override
            public String getName() {
                return "local-test";
            }

            @Override
            public AIProviderType getType() {
                return AIProviderType.LOCAL;
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public AIResponse generate(AIRequest request) {
                return AIResponse.success("local");
            }
        };
    }

    @Test
    public void codingTaskUsesCloudWhenAvailable() {
        AIProviderRegistry registry =
                new AIProviderRegistry();

        registry.register(localProvider());
        registry.register(cloudProvider());

        AIProviderRouter router =
                new AIProviderRouter(registry);

        ProviderSelection selection =
                router.route(AITaskType.CODING);

        assertTrue(selection.isSuccess());
        assertEquals(
                "cloud-test",
                selection.getProvider().getName()
        );
    }

    @Test
    public void privateTaskPrefersLocal() {
        AIProviderRegistry registry =
                new AIProviderRegistry();

        registry.register(cloudProvider());
        registry.register(localProvider());

        AIProviderRouter router =
                new AIProviderRouter(registry);

        ProviderSelection selection =
                router.route(AITaskType.PRIVATE);

        assertTrue(selection.isSuccess());
        assertEquals(
                "local-test",
                selection.getProvider().getName()
        );
    }

    @Test
    public void localOnlyRejectsCloudFallback() {
        AIProviderRegistry registry =
                new AIProviderRegistry();

        registry.register(cloudProvider());

        AIProviderRouter router =
                new AIProviderRouter(registry);

        ProviderSelection selection =
                router.route(
                        AITaskType.GENERAL,
                        true
                );

        assertFalse(selection.isSuccess());
    }

    @Test
    public void routerReportsNoProvider() {
        AIProviderRouter router =
                new AIProviderRouter(
                        new AIProviderRegistry()
                );

        ProviderSelection selection =
                router.route(AITaskType.GENERAL);

        assertFalse(selection.isSuccess());
        assertEquals(
                "No available AI provider",
                selection.getReason()
        );
    }
}
