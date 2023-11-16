package io.billie.organisations.data

import java.util.UUID

class OrganizationNotFoundException(val organizationId: UUID) : RuntimeException()