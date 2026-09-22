package com.atlas.app.improvement;

import com.atlas.app.ai.AIProvider;
import com.atlas.app.ai.AIProviderBootstrap;
import com.atlas.app.ai.AIProviderRouter;
import com.atlas.app.ai.AITaskType;
import com.atlas.app.ai.ProviderSelection;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AtlasGeminiIntegrationTest {

    @Test
    public void routerCanSelectAvailableGeminiProvider() {

        AIProviderRouter router =
                AIProviderBootstrap.createRouter();

        assertNotNull(router);

        ProviderSelection selection =
                router.route(AITaskType.CODING);

        assertNotNull(selection);

        assertTrue(
                "No available AI provider",
                selection.isSuccess()
        );

        AIProvider provider =
                selection.getProvider();

        assertNotNull(provider);
        assertTrue(provider.isAvailable());
    }
}
