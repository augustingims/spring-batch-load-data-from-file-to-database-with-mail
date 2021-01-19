package com.teamdevsolution.batch.services.impl;

import com.teamdevsolution.batch.domain.Planning;
import com.teamdevsolution.batch.services.MailContentGenerator;
import freemarker.core.ParseException;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;

public class MailContentGeneratorImpl implements MailContentGenerator {

    private final Template template;

    public MailContentGeneratorImpl(final Configuration conf)
            throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        super();
        template = conf.getTemplate("planning.ftl");
    }

    @Override
    public String generate(final Planning planning) throws TemplateException, IOException {
        StringWriter sw = new StringWriter();
        template.process(planning, sw);
        return sw.toString();
    }
}
