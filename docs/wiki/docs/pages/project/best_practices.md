# <p align="center"> PR Advices </p>

You'll see that once you open a PR some checks will be made, they are all essential to be passed before you merge your branch, so, make sure they are all passing.

One exception is snyk, which sometimes have some problems that can't be solved now, in this case you can just bypass.

# <p align="center"> Best Practices </p>
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
