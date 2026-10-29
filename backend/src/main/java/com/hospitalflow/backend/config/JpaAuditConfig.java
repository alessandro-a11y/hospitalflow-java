package com.hospitalflow.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita a auditoria JPA para preenchimento automático de
 * {@code createdAt} e {@code updatedAt} nas entidades que estendem
 * {@link com.hospitalflow.backend.common.BaseEntity}.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
