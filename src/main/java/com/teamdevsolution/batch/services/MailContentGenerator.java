package com.teamdevsolution.batch.services;

import com.teamdevsolution.batch.domain.Planning;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface MailContentGenerator {

    String generate(Planning planning) throws TemplateException, IOException;
}
