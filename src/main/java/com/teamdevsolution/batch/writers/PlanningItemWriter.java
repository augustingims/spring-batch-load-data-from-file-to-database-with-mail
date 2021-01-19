package com.teamdevsolution.batch.writers;

import com.teamdevsolution.batch.domain.Planning;
import com.teamdevsolution.batch.services.MailContentGenerator;
import com.teamdevsolution.batch.services.PlanningMailSenderService;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class PlanningItemWriter implements ItemWriter<Planning> {

    private final PlanningMailSenderService planningService;

    private final MailContentGenerator mailContentGenerator;

    public PlanningItemWriter(final PlanningMailSenderService planningService,
                              final MailContentGenerator mailContentGenerator) {
        super();
        this.planningService = planningService;
        this.mailContentGenerator = mailContentGenerator;
    }

    @Override
    public void write(List<? extends Planning> plannings) throws Exception {
        for (Planning planning : plannings) {
            String content = mailContentGenerator.generate(planning);
            planningService.send(planning.getFormateur().getAdresseEmail(), content);
        }
    }
}
