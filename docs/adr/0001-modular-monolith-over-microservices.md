# ADR-0001: Modular monolith over microservices

## Status

Accepted

## Context

GameHub needs an architecture that supports a system with several distinct
domains (identity, player, matchmaking, match, leaderboard, economy) while
being built and maintained by a single developer with limited time.

Microservices are a common choice for systems with independent domains. They
solve real problems: independent deployment, independent scaling, and
organisational separation between teams that own different services.

None of those problems currently apply here. There is one developer, one
deployment target, and no organisational boundaries. Adopting microservices
on day one would introduce network calls between services, distributed
transactions, service discovery, and independent deployment pipelines — all
of which are cost without a corresponding benefit at this scale.

At the same time, the domains should not be entangled. A design where every
class can call every other class would make future extraction impossible and
would not demonstrate deliberate structure to a reviewer.

## Decision

Build GameHub as a **modular monolith**: a single deployable unit, organised
into domain modules with explicit boundaries between them.

Concretely, this is enforced through **package-by-feature**: each domain is a
top-level package (`identity`, `player`, `matchmaking`, ...) rather than
grouping all controllers, services, and repositories by technical layer.
Within each module, the layered structure (controller, service, repository)
still applies.

A module is extracted into a separate service **only when there is a concrete
reason to**. The first planned extraction is the `stats-worker`, because
post-match processing is asynchronous, consumes events rather than HTTP
requests, and scales differently from the main API.

## Consequences

**Positive**

- Low operational complexity: one process, one deployment, local method calls
  instead of network calls.
- The domain boundaries are visible in the folder structure, which documents
  the intended architecture without extra diagrams.
- Extraction stays cheap: a well-bounded package can be lifted into its own
  service when justified, rather than untangling classes spread across
  technical layers.

**Negative / trade-offs accepted**

- All modules share a single process and a single deployment. A change to one
  module requires redeploying the whole application.
- Module boundaries are a convention, not a hard constraint enforced by the
  compiler. Discipline is required to avoid a module reaching directly into
  another module's internals.
- Some job listings ask explicitly for microservices experience. This is
  mitigated by (a) documenting the decision here, which shows it was a
  conscious trade-off rather than a gap in knowledge, and (b) the planned
  extraction of `stats-worker`, which exercises real service-to-service
  communication over Kafka.
