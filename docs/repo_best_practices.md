# Repository Best Practices
### Branches: [Jira Issue Code] - type/name
  **Ex.: **
  ```
   [CSDD-18] - feature/planet-creation
   [CSDD-18] - hotfix/production-rollback
   [CSDD-18] - bugfix/planet-creation
  ```

### Commits
**Model:**
```
type: brief description
- detailed description
- detailed description
```

**Ex.:**
```
feature: create planet operations
- Add planet operations contratct
- Add post endpoint to create planets
- Add update planet endpoint

chore: create planet docker operations
- Add dev environment to planets
- Add dev tools: localstack and mongo to docker-compose
- Add docker compose for testing

refactor: remove code smells
- Remove public modifiers in tests
- Make variables final
```
