package dev.thillainirmal.sentinelops.agent;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RunbookKnowledgeBaseTest {

    private final RunbookKnowledgeBase knowledgeBase = new RunbookKnowledgeBase();

    @Test
    void retrievesKafkaRunbookForConsumerLag() {
        var runbook = knowledgeBase.retrieve("Consumer group backlog and Kafka partition lag are increasing");

        assertThat(runbook.id()).isEqualTo("kafka-lag");
    }

    @Test
    void similarityRewardsSemanticTokenOverlap() {
        double related = knowledgeBase.similarity("payment gateway timeout", "payment authorization gateway failure");
        double unrelated = knowledgeBase.similarity("payment gateway timeout", "css layout button color");

        assertThat(related).isGreaterThan(unrelated);
    }
}
