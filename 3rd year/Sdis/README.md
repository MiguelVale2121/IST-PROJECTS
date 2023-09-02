# SDIS Project - DistLedger

## Phase 1
- The service is provided by a single server.
- Requests are accepted at a fixed address/port known to all clients.

## Phase 2
- The service is provided by two servers: a primary and a secondary.
- Write operations can only be performed on the primary server.
- The primary server propagates the write operation to the secondary before responding to the client.
- Read operations can be executed on either server.
- The system operates without a primary (for read operations) if the primary server is unavailable, and recovers with its previous state.

## Phase 3
- The service is provided by two servers that share state using a causal order model for write operations.
- Causal ordering is implemented using the gossip architecture.
- Servers can respond immediately to write operation requests if causal dependencies are satisfied locally.
- Gossip propagation of write operations occurs upon request by the admin client.

These phases represent the evolution of the service architecture, starting from a single server and progressing to redundancy, fault tolerance, and causal ordering mechanisms.

## Sdis_project

Grade: 18.90
