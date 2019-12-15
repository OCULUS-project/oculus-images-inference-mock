package pl.poznan.put.oculus.imagesinferencemock

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import pl.poznan.put.oculus.imagesinferencemock.model.FactSource
import pl.poznan.put.oculus.imagesinferencemock.model.GrfIrf
import pl.poznan.put.oculus.imagesinferencemock.model.JobEvent
import pl.poznan.put.oculus.imagesinferencemock.model.SourceFactEvent

@SpringBootApplication
class ImagesInferenceMockApplication

fun main(args: Array<String>) {
    runApplication<ImagesInferenceMockApplication>(*args)
}

@Component
class Mock (
        private val factsKafkaTemplate: KafkaTemplate<String, SourceFactEvent>,
        private val jobsKafkaTemplate: KafkaTemplate<String, JobEvent>
) {
    @KafkaListener(topics = ["jobs"], groupId = "1")
    fun receive(event: JobEvent) {
        if(event.type == "NEW") {
            logger.info("received new job event for job ${event.jobId}")
            generateRandomFacts(event.jobId)
            sendFactsGenerationEndedEvent(event.jobId)
        }
    }

    private fun generateRandomFacts(jobId: String) {
        val n = (150000..1500000).random()
        repeat(n) {
            factsKafkaTemplate.send("sourceFacts", SourceFactEvent(
                    "head_$it",
                    listOf("set_$it"),
                    false,
                    GrfIrf((1..100).random() / 100.0, (1..100).random() / 100.0),
                    jobId,
                    FactSource("some_image_id")
            ))
        }
        logger.info("generated $n random facts for job $jobId")
    }

    private fun sendFactsGenerationEndedEvent(jobId: String) {
        jobsKafkaTemplate.send("jobs", JobEvent("IMAGES_INFERENCE_ENDED", jobId))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Mock::class.java)
    }
}
