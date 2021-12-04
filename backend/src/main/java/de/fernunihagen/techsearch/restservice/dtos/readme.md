Die clientseiteigen DTOs in TypeScript können mit einem Maven-Aufruf erzeugt werden:
```
..\mvnw process-classes
```

Die Klassen landen dann in 
```
target\typescript-generator\backend.d.ts
```