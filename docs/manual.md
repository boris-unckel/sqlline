# sqlline — Agent Manual

Machine-oriented reference for driving `sqlline` from another program. Derived from source. Covers invocation, non-interactive patterns, exit semantics, commands, properties, output formats, limitations, and runtime dependencies.

## 1. Purpose

JDBC shell. Connects to any database with a JDBC driver, executes SQL and metadata commands, emits results in selectable text formats. One Java process, one JVM classpath, no daemon.

## 2. Invocation

### 2.1 Binary

- Main class: `sqlline.SqlLine`
- Package ships a fat jar: `sqlline-<version>-jar-with-dependencies.jar` (contains JLine and sqlline, but **not** JDBC drivers — you supply those).
- Typical launch:
  ```
  java -cp sqlline-<version>-jar-with-dependencies.jar:<driver>.jar sqlline.SqlLine [args]
  ```

### 2.2 Argument grammar

Parsed in `SqlLine.initArgs` (src/main/java/sqlline/SqlLine.java:325). Order of flags does not matter except that value-taking flags must be followed by their value as the next token.

| Flag | Value | Effect |
|------|-------|--------|
| `-h`, `--help` | — | Print usage, exit with `ARGS` |
| `-u <url>` | JDBC URL | Connection URL |
| `-n <user>` | string | User name |
| `-p <password>` | string | Password (plaintext on command line — see §8.3) |
| `-d <driverClass>` | FQN | Explicit JDBC driver class; registers it before connect |
| `-e <sqlOr!cmd>` | string | Execute one command; may be repeated. Disables color. Implies exit after execution. |
| `-f <file>` | path | Run script file via `!run`, then `!quit` |
| `-log <file>` | path | Tee session to file via `!record` |
| `-nn <nickname>` | string | Nickname for the connection |
| `-ac <class>` | FQN | Custom `sqlline.Application` subclass |
| `-ph <class>` | FQN | Custom `sqlline.PromptHandler` subclass |
| `-ch <class1,class2,…>` | FQNs | Load custom `CommandHandler` classes |
| `-c <file>` | path | Connection-config file; pick a connection when `!connect -c <name>` is used |
| `-cn <file>` | path | Same file, but also use it as the name source (`-cn` = `-c` + auto-pick first entry) |
| `--<prop>[=<value>]` | — | Set session property; see §5. Without `=`, value is `true`. |
| `--no-<prop>` | — | Set boolean property to `false` |
| `--silent` | — | Shorthand: prompt="", rightPrompt="", silent=true, verbose=false |
| `<file>` (positional) | path | `.properties` file loaded via `!properties` |

Any other `-X` token ⇒ `Status.ARGS`. A value-taking flag at the end of argv with no following token ⇒ `Status.ARGS`.

### 2.3 Exit codes

`sqlline.SqlLine.start` ends with `System.exit(status.ordinal())` unless system property `sqlline.system.exit=true` is set (SqlLine.java:272).

| Code | Enum | When |
|------|------|------|
| 0 | `Status.OK` | Normal completion |
| 1 | `Status.ARGS` | Bad CLI arguments, `--help` |
| 2 | `Status.OTHER` | Anything else (script failure with `-f`, uncaught runtime error, etc.) |

**Important for agents — last-command-wins semantics:** Both `-e` and `-f` can return `Status.OTHER` (exit 2) on failure, but the propagation rules differ:

- **`-f <script>`** is fail-fast: on the first failing statement the script aborts and the process exits with `OTHER`. `initArgs` dispatches `!run <file>` at SqlLine.java:497; inside that, `runCommands` breaks out of the loop on the first failing statement unless `force=true` (SqlLine.java:1840-1843), the callback is set to failure, and `initArgs` promotes it to `Status.OTHER` at SqlLine.java:498-500. The status survives to the enum-to-exit conversion at SqlLine.java:633 because the follow-up `!quit` is dispatched with a throwaway callback and leaves the main callback untouched.
- **`-e <cmd>` (possibly repeated)** is *last-command-wins*: every `-e` runs, and the final exit code reflects only the status of the **last** `-e` (`DispatchCallback` has a single state field that each dispatch overwrites; `begin()` evaluates `callback.isFailure()` once after the loop at SqlLine.java:633). A single `-e "bad"` ⇒ `OTHER`; but `-e "bad" -e "good"` ⇒ `OK`.

Agents that chain multiple `-e` and need reliable failure detection should either use a single `-f` script, or issue one `-e` per process invocation.

## 3. Non-interactive patterns for agents

### 3.1 One-shot query, capture stdout

```
java -cp sqlline-fat.jar:pg.jar sqlline.SqlLine \
  -u jdbc:postgresql://host/db -n user -p pw \
  --outputformat=csv --silent=true --showHeader=false \
  -e "SELECT id, name FROM t WHERE id = 42;"
```

Notes:
- `--silent=true` removes the `sqlline> ` prompt from stdout.
- `--showHeader=false` drops the column-name row.
- `--outputformat=csv|tsv|json` for parseable output.
- `-e` disables `--color` unconditionally (SqlLine.java:482), so no ANSI to strip.

### 3.2 Script file, fail-fast exit code

```
java … sqlline.SqlLine -u jdbc:… -f migration.sql
# exit 0 on clean run, exit 2 on any failing statement
```

A `-f` script error path reliably yields exit 2 (fail-fast on first failing statement). Prefer this form over chained `-e` when exit-code-based success detection is needed.

### 3.3 Stdin pipe

```
echo "!tables" | java … sqlline.SqlLine -u jdbc:…
```

When neither `-e` nor `-f` is given, sqlline consumes stdin as interactive input. EOF triggers shutdown. The prompt still prints unless `--silent=true`. Exit code reflects only the last-dispatched command's status (same last-command-wins rule as `-e`, since `runningScript` is false and the interactive while-loop does not abort on failure).

### 3.4 Multiple statements

- `-e` may be repeated: `-e "SQL1;" -e "SQL2;"` — each dispatched in order.
- Inside a `-f` script, terminate each SQL statement with `;`. Meta-commands start with `!` and are line-oriented (no trailing `;` needed).

### 3.5 Avoiding history / config pollution

```
--historyFile=/dev/null --connectionConfig= --maxHistoryFileRows=0
```

Also consider `-Dsqlline.system.exit=true` and run inside a clean `HOME=/tmp/sqlline-sandbox` to prevent `~/.sqlline/` creation.

## 4. Meta-commands

All meta-commands begin with `!`. Registered in `Application.getCommandHandlers` (src/main/java/sqlline/Application.java:243). Aliases shown in parentheses.

### 4.1 Connection

| Command | Args | Notes |
|---------|------|-------|
| `!connect` (`!open`) | `<url> [user] [pass] [driverClass]` or `-p driver X -p user Y -p password Z <url>` or `-c <file> <name>` or `-cn <file>` | Open a connection |
| `!close` | — | Close current connection |
| `!closeall` | — | Close all connections |
| `!reconnect` | — | Re-open the current connection |
| `!nickname` | `<name>` | Label active connection |
| `!scan` | — | Enumerate driver classes on classpath |
| `!save` | — | Write current session properties to `~/.sqlline/sqlline.properties` (not connection URLs — misnamed historically) |
| `!showconfconnections` | — | List named connections from config file |
| `!rereadconfconnections` | `<file>` | Reload config file |

### 4.2 Transaction control

| Command | Args | Notes |
|---------|------|-------|
| `!autocommit` | `on\|off` | Default on |
| `!commit` | — | COMMIT |
| `!rollback` | — | ROLLBACK |
| `!readonly` | `on\|off` | `Connection.setReadOnly` |
| `!isolation` | `TRANSACTION_NONE \| TRANSACTION_READ_UNCOMMITTED \| TRANSACTION_READ_COMMITTED \| TRANSACTION_REPEATABLE_READ \| TRANSACTION_SERIALIZABLE` | — |

### 4.3 Metadata (require active connection)

| Command | Args | Notes |
|---------|------|-------|
| `!tables` | — | `DatabaseMetaData.getTables` |
| `!schemas` | — | `getSchemas` |
| `!columns` | `<table>` | `getColumns` |
| `!indexes` | `<table>` | |
| `!primarykeys` | `<table>` | |
| `!exportedkeys` / `!importedkeys` | `<table>` | |
| `!procedures` | — | |
| `!typeinfo` | — | `getTypeInfo` |
| `!describe` | `<table>` | `SELECT * FROM … WHERE 1=0` probe |
| `!dbinfo` | — | Product/version + many capability flags |
| `!metadata` | `<method> [args…]` | Reflectively invoke any `DatabaseMetaData` method |
| `!nativesql` | `<sql>` | `Connection.nativeSQL` |

### 4.4 Execution

| Command | Args | Notes |
|---------|------|-------|
| `!sql` | `<sql>` | Explicit SQL (rarely needed — bare SQL works) |
| `!call` | `<sql>` | CALL via `CallableStatement` |
| `!run` | `<file>` | Execute SQL/meta from file |
| `!script` | `<file>` | Echo upcoming input to file (no execute). Toggle off with `!script` (no arg) |
| `!record` | `<file>` | Tee transcript to file. Toggle off with `!record` (no arg) |
| `!batch` | — | Start/flush a batch (JDBC `addBatch`/`executeBatch`) |
| `!go` (`#`) | `[n]` | Switch to connection #n from `!list` |
| `!list` | — | List open connections |
| `!all` | `<sql>` | Run SQL against every open connection |
| `!rerun` (`/`) | `[n]` | Re-run history entry |
| `!history` | — | Print history |

### 4.5 Configuration

| Command | Args | Notes |
|---------|------|-------|
| `!set` | `[<prop>] [<value>]` | No args ⇒ dump all; one arg ⇒ show one; two ⇒ assign |
| `!reset` | `[<prop>]` | Restore default (or all) |
| `!properties` | `<file>` | Load `.properties` into session |
| `!outputformat` | `<name>` | See §6 |
| `!brief` | — | Shorthand for `outputformat=vertical` |
| `!verbose` | — | Toggle verbose |
| `!appconfig` | `<FQN>` | Load custom `Application` |
| `!prompthandler` | `<FQN>` | Load custom prompt handler |
| `!commandhandler` | `<class1,class2,…>` | Load custom handlers |

### 4.6 Lifecycle

| Command | Args | Notes |
|---------|------|-------|
| `!quit` (`!done`, `!exit`) | — | Exit |
| `!help` (`!?`) | `[cmd]` | Usage |
| `!manual` | — | Show bundled manual |
| `!rehash` | — | Rebuild completion tables |
| `!resize` | — | Re-detect terminal size |
| `!dropall` | `[schema]` | `DROP TABLE` every table in schema (destructive). Always prompts with a hardcoded y/n question via the JLine terminal; the `confirm`/`confirmPattern` session properties do **not** gate this prompt. The generated `DROP TABLE` statements still go through the SQL dispatch path, so they are subject to `confirmPattern` if `confirm=true`. |

## 5. Session properties

Enumerated in `sqlline.BuiltInProperty`. Set via `--name=value` on CLI, `!set name value` at runtime, or in a `.properties` file. Types: `BOOLEAN`, `CHAR`, `FILE_PATH`, `INTEGER`, `STRING` (some `STRING` enumerated).

| Property | Type | Default | Purpose |
|----------|------|---------|---------|
| `autoCommit` | BOOLEAN | true | |
| `autoPairing` | BOOLEAN | true | Bracket auto-pair (interactive only) |
| `autoResize` | BOOLEAN | false | Re-detect terminal size on each prompt |
| `autoSave` | BOOLEAN | false | Persist property changes to `~/.sqlline/sqlline.properties` |
| `color` | BOOLEAN | false | ANSI colors (forced off by `-e`) |
| `colorScheme` | STRING enum | — | Syntax-highlight theme |
| `confirm` | BOOLEAN | false | Confirm statements matching `confirmPattern` |
| `confirmPattern` | STRING regex | `^(?i:(DROP\|DELETE))` | |
| `connectionConfig` | FILE_PATH | "" | File used by `!connect -c name` |
| `csvDelimiter` | STRING | `,` | Stored verbatim and handed to `SeparatedValuesOutputFormat`; no validation. Values containing `"` or `'` are accepted but produce malformed CSV because the format quotes each field with `csvQuoteCharacter`. |
| `csvQuoteCharacter` | CHAR | `'` | |
| `dateFormat` / `timeFormat` / `timestampFormat` / `numberFormat` | STRING | `default` | `default` = JDBC-native `toString`; else `SimpleDateFormat`/`DecimalFormat` pattern |
| `escapeOutput` | BOOLEAN | false | Escape control chars in output |
| `fastConnect` | BOOLEAN | true | Skip re-registering drivers on reconnect |
| `force` | BOOLEAN | false | Continue after errors in scripts |
| `headerInterval` | INTEGER | 100 | Repeat header every N rows (forced to -1 with `-e`) |
| `historyFile` | FILE_PATH | `~/.sqlline/history` | |
| `historyFlags` | STRING | `-d` | JLine history flag |
| `incremental` | BOOLEAN | false | Stream rows as fetched vs. buffer whole result |
| `incrementalBufferRows` | INTEGER | 1000 | Buffer size when non-incremental |
| `isolation` | STRING enum | `TRANSACTION_REPEATABLE_READ` | |
| `keepSemicolon` | BOOLEAN | false | Keep `;` in SQL sent to driver |
| `liveTemplates` | FILE_PATH | "" | |
| `maxColumnWidth` | INTEGER | -1 | -1 = unlimited |
| `maxWidth` / `maxHeight` | INTEGER | 80, then overwritten by `terminal.getWidth()` / `getHeight()` when a real terminal is attached | Writable (`!set maxWidth 200`), but `couldBeStored=false` so never persisted to `sqlline.properties`. Re-detected each prompt if `autoResize=true`. |
| `maxHistoryRows` / `maxHistoryFileRows` | INTEGER | JLine defaults | |
| `mode` | STRING | `emacs` | `emacs` or `vi` — line-edit mode |
| `nullValue` | STRING | `default` | Rendered for SQL NULL |
| `prompt` | STRING | `sqlline> ` | |
| `promptScript` | STRING | "" | Script for dynamic prompt |
| `readOnly` | BOOLEAN | false | |
| `rightPrompt` | STRING | "" | |
| `rowLimit` | INTEGER | 0 | 0 = unlimited; else `Statement.setMaxRows` |
| `scriptEngine` | STRING | `nashorn` | JSR-223 engine for prompt scripts; **Nashorn is absent from JDK 15+ by default** (see §8) |
| `showCompletionDesc` | BOOLEAN | true | |
| `showElapsedTime` | BOOLEAN | true | |
| `showHeader` | BOOLEAN | true | |
| `showLineNumbers` | BOOLEAN | false | |
| `showNestedErrs` | BOOLEAN | false | Chain through `SQLException.getNextException` |
| `showTypes` | BOOLEAN | false | Print column types with header |
| `showWarnings` | BOOLEAN | true | |
| `silent` | BOOLEAN | false | Suppress prompt/banner |
| `strictJdbc` | BOOLEAN | false | Disable workarounds for non-conforming drivers |
| `tableStyle` | STRING enum | — | Table rendering style |
| `timeout` | INTEGER seconds | -1 | Passed unchanged to `Statement.setQueryTimeout(int)` (SqlLine.java:1782), whose contract is seconds. -1 (the default) disables the call. |
| `trimScripts` | BOOLEAN | true | Strip leading/trailing whitespace from script lines |
| `useLineContinuation` | BOOLEAN | true | Multi-line SQL via `\` continuation |
| `verbose` | BOOLEAN | false | Print stacktraces on exceptions |
| `version` | STRING | — | **Read-only** (only truly read-only property) |
| `connectInteractionMode` | STRING enum | `askCredentials` | `askCredentials` / `notAskCredentials` / `useNPTogetherOrEmpty` |
| `outputFormat` | STRING enum | `table` | Active output format; enumerated in §6. Settable via `--outputformat=<name>` or `!outputformat <name>`. |
| `propertiesFile` | FILE_PATH | `~/.sqlline/sqlline.properties` | File `!save` writes to and `!properties` / the startup loader reads. |

## 6. Output formats

Registered in `Application.getOutputFormats` (Application.java:201).

| Name | Class | Notes |
|------|-------|-------|
| `table` (default) | `TableOutputFormat` | ASCII box-drawing table |
| `vertical` | `VerticalOutputFormat` | One column per line; good for wide rows |
| `ansiconsole` | `AnsiConsoleOutputFormat` | Like `table` but ANSI-colored |
| `csv` | `SeparatedValuesOutputFormat` | Comma-separated; obeys `csvDelimiter`/`csvQuoteCharacter` |
| `tsv` | `SeparatedValuesOutputFormat` | Tab-separated |
| `json` | `JsonOutputFormat` | Array of objects |
| `xmlattrs` (alias `xmlattr`) | `XmlAttributeOutputFormat` | Columns as attributes |
| `xmlelements` | `XmlElementOutputFormat` | Columns as child elements |

Select: `--outputformat=csv` or `!outputformat csv`.

## 7. Connection specification

### 7.1 Inline

```
!connect jdbc:postgresql://host/db user pass org.postgresql.Driver
```
or keyed:
```
!connect -p driver org.postgresql.Driver -p user U -p password P jdbc:postgresql://host/db
```

### 7.2 Named from config file

Config file syntax (parsed in `ConnectionConfigParser.java:34`):

```
myconn:
  url: jdbc:postgresql://host/db
  user: U
  password: P
  driver: org.postgresql.Driver
global-conf:
  maxWidth: 200
```

Rules:
- First token at column 0 starts a new named block.
- Subsequent `  key: value` lines belong to that block.
- Lines starting with `#` and blank lines ignored.
- `global-conf` block supplies session properties applied on connect.

Invoke: `!connect -c <file> myconn` or CLI `-cn <file>` (picks first block).

### 7.3 Driver loading

Drivers are discovered via JDBC 4 `ServiceLoader` (`META-INF/services/java.sql.Driver`) on the launcher classpath. `!scan` walks the classpath and lists them. **There is no `!addlocaldriverjar`** — you must add driver JARs to the JVM classpath before starting sqlline.

## 8. Limitations and caveats

### 8.1 Exit-code reliability
- `-e` and interactive stdin use **last-command-wins**: exit code reflects only the last dispatched command. `-e "bad"` ⇒ 2, but `-e "bad" -e "good"` ⇒ 0. See §2.3.
- `-f` is fail-fast: first failing statement aborts the script and yields exit 2.
- For multi-statement agent runs requiring reliable failure detection, prefer one `-f` script, or one process per `-e`.

### 8.2 Charset and locale
- All I/O is hardcoded UTF-8 (`StandardCharsets.UTF_8` across SqlLine.java, ConnectionConfigParser). No way to override.
- ResourceBundle and number/date parsing use `Locale.ROOT` (SqlLine.java:63). User's system locale is ignored.

### 8.3 Password on CLI
- `-p` takes the password as a literal argv token — visible in `ps`, shell history, and most process snapshots. For agents that need secrecy: pass via stdin/`!connect`, via `-c <file>` (mode-0600 file), or via a custom `Application` that reads a secret store.

### 8.4 Script engine
- `promptScript` relies on JSR-223. Default `scriptEngine=nashorn`. Nashorn was removed from the JDK in Java 15. Provide your own (`org.openjdk.nashorn:nashorn-core` on classpath) or avoid `promptScript`.

### 8.5 Runtime Java version
- Built with `--release 21`; will not run on JDK < 21 (enforced by `requireJavaVersion=[21,)` in pom.xml). Confirm with `java -version` before launch.

### 8.6 CSV delimiter
- `csvDelimiter` is stored verbatim with **no validation**. `SeparatedValuesOutputFormat` uses it as a raw string separator and wraps every field with `csvQuoteCharacter`. A delimiter that contains either quote character (`'` or `"`) or the current `csvQuoteCharacter` produces output that most CSV parsers cannot round-trip. Stick to single characters (`,`, `|`, `\t`, etc.).
- The `UnsupportedOperationException` at SqlLine.java:1347 comes from the internal `split(line, delim, limit)` helper used to parse SqlLine **command lines**; it is not part of the CSV output path.

### 8.7 `!quit` side effects
- `!quit` initiates exit and closes all connections. After `!quit` no further commands are processed, even on the same line.

### 8.8 `!dropall`
- Destructive. The command itself **always** prompts for `y/n` via the JLine terminal (`Commands.dropall` → `getUserAnswer`, Commands.java:549-552); this prompt is hardcoded and is **not** controlled by the `confirm` / `confirmPattern` session properties. In a non-interactive run (piped stdin, `-e`, `-f` with no TTY) the prompt blocks and the command will never answer itself — avoid `!dropall` from agents unless you attach a real terminal or replace the handler with a custom `CommandHandler`.
- Once answered, `!dropall` synthesises `DROP TABLE` statements and runs them through the normal SQL dispatch path, which **does** check `confirmPattern` when `confirm=true`. So `confirm=true` causes a *second* confirmation prompt per table.

### 8.9 Read-only properties
- Only `version` is truly read-only (`isReadOnly=true` in `BuiltInProperty`); attempting `!set version x` logs `property-readonly` and is a no-op.
- `maxWidth` and `maxHeight` are writable at runtime (`couldBeStored=false, isReadOnly=false`) but are (a) overwritten by terminal detection at startup and on every prompt if `autoResize=true`, and (b) excluded from `sqlline.properties` persistence. A manual `!set maxWidth 200` sticks only until the next auto-resize or reconnect.

### 8.10 Custom handlers
- `-ac`, `-ph`, `-ch` load classes by name from the launcher classpath. The required public constructor differs by handler:
  - `-ac <Application-subclass>` → no-arg constructor (`Commands.appconfig`, Commands.java:2058-2059).
  - `-ph <PromptHandler-subclass>` → single-arg `(SqlLine)` constructor (`Commands.prompthandler`, Commands.java:2087-2088). Pass `default` to restore the built-in handler.
  - `-ch <CommandHandler-subclass[,...]>` → single-arg `(SqlLine)` constructor (`Commands.commandhandler`, Commands.java:1857-1859). A handler whose `getNames()` collides with any already-registered name is silently skipped.
- Load failures are reported on stderr via a throwaway `DispatchCallback` in `initArgs` and startup continues with defaults — this does not change the exit code.

### 8.11 Transaction behaviour
- Default `autoCommit=true`. If the target DB does not support autocommit with certain DDL (some vendors), expect surprises unless you issue `!autocommit off` first.

### 8.12 Colors
- `--color=true` enables ANSI. Redirect-safe: `-e` force-disables color regardless of `--color`. For stdin piping, set `--color=false` explicitly — sqlline does not auto-detect a non-TTY stdout.

### 8.13 Concurrency
- Not thread-safe; single-threaded per JVM process. One interactive session per process.

## 9. Dependencies

### 9.1 Runtime (embedded in fat jar)

| Artifact | Version | Purpose |
|----------|---------|---------|
| `org.jline:jline-*` | 4.x | Terminal, line editing, syntax highlighting |
| `net.hydromatic:scott-data-hsqldb` | — | **Test only**, not bundled |

JLine submodules bundled: `jline-terminal`, `jline-reader`, `jline-terminal-jni` (cross-platform native terminal), `jline-builtins`, `jline-console`.

### 9.2 Runtime (not bundled — supply yourself)

- **JDK 21+** — required per `<release>21</release>` in pom.xml.
- **JDBC driver jar(s)** for the target database — must appear on the classpath alongside sqlline.
- Optional: `org.openjdk.nashorn:nashorn-core` if `promptScript` is used on JDK 15+.

### 9.3 Build-only (not needed at runtime)

jmockit, junit-jupiter, hamcrest, hsqldb, h2, checkstyle, forbiddenapis, maven-\*-plugin. Listed in `pom.xml` with `<scope>test</scope>` or as build plugins.

## 10. Minimal agent recipe

```bash
CP="sqlline-fat.jar:postgresql-42.x.jar"
java -Dsqlline.system.exit=true -cp "$CP" sqlline.SqlLine \
  -u "jdbc:postgresql://${HOST}/${DB}" \
  -n "${USER}" -p "${PASS}" \
  --silent=true \
  --outputformat=json \
  --showElapsedTime=false \
  --historyFile=/dev/null \
  -f /tmp/job.sql
echo "exit=$?"   # 0 OK, 2 on SQL error in job.sql
```

Prefer `-f` over chained `-e` when you need reliable exit codes (fail-fast vs. last-command-wins). Prefer `json`/`csv` when you need to parse output. Prefer `--silent=true --showElapsedTime=false --showHeader=false` when you need bytes-exact output.
