package com.samsonan.service;

import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ConverterService.class).to(ConverterServiceImpl.class);
	}
}
