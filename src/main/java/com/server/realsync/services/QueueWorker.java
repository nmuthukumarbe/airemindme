package com.server.realsync.services;

import java.util.List;

import org.springframework.stereotype.Component;

import com.server.realsync.entity.MessageQueue;
import com.server.realsync.entity.QueueChannel;

/**
 * 
 * 
 */

@Component
public class QueueWorker {

	private final MessageQueueService queueService;
	// private final WhatsappService whatsappService;

	public QueueWorker(MessageQueueService queueService) {// WhatsappService whatsappService

		this.queueService = queueService;
		// this.whatsappService = whatsappService;
	}

	//@Scheduled(fixedDelay = 5000)
	public void processQueue() {

		List<MessageQueue> jobs = queueService.fetchJobs(50);

		for (MessageQueue job : jobs) {

			try {

				if (job.getChannel() == QueueChannel.WHATSAPP) {

					// whatsappService.send(job.getEntityEntryId());
				}

				queueService.markDone(job);

			} catch (Exception ex) {

				queueService.markFailed(job, ex.getMessage());
			}
		}
	}
}