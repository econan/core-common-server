package com.impacsys.core.common.server.controller;

/**
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.10
 *
 */
public class CommonMvcMapping {

	public static class Url {

		public static final String PATH_ID = "/{id}";

		/* TBD: Select all list */
		public static final String ALL = "/all";

		/* TBD: Search a Entity by given parameters */
		public static final String SEARCH = "/search";

		private Url() {

		}
	}

	public class ModelKey {

		public static final String ID = "id";
		public static final String KEY_ERRORS = "errors";

		private ModelKey() {

		}
	}
}
