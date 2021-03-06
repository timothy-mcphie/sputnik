![sputnik](http://touk.github.io/sputnik/images/logo-color-bgtransparent-small.png)

> Static code review for your Gerrit and Stash patchsets.

This version of Sputnik runs pitest on a build passed to CI.
Note, currently this version of sputnik is only configured to support running Pitest and extracting the pit\_diff. Using with other tools enabled is currently unstable.

## Usage


Typical configuration file looks like this:

```properties
connector.type=gerrit
connector.host=your.host.com
connector.path=/gerrit
connector.port=8080
connector.username=sputnik
connector.password=PassWd
connector.useHttps=false
pit.enabled=true
pit.filter=package.to.mutate.*
pit.path=path/to/sputnik_pit.py
```


- The configuration tool has three pit specific args. pit.enabled defaults to true, and pit.path defaults to a default path for the `sputnik_pit.py` script which is `/opt/sputnik/pit_diff_tool/sputnik_pit.py`
- pit.filter is MANDATORY, this is the package filter used to specify which classes and tests pit will be mutating/using. If it is two general, eg org.\*, pit attempts to mutate itself and will be unsuccessful, supply a fully qualified package name followed by the kleene star eg org.apache.commons.collections4.\*

## Pit/Sputnik Specific Requirements

- See [pit_diff_tool](https://github.com/timothy-mcphie/pit_diff_tool) for further documentation on installing and using `sputnik_pit.py` tool in conjunction with the `pit_diff` package.
- Use the same installation instructions as below (building with gradle)
- This pit/sputnik version slots in after a Jenkins build has occurred (only tested on Jenkins). For mutation reports to persist on the CI server (enabling diffs to be run), the build must be checked out on jenkins to a local directory, not a workspace which is cleaned. A path can be specified with Check out to a sub directory in the Advanced section of the Source Code Management when configuring a Jenkins project. Both this and the option, Use Custom Workspace in the General section of project configuration, pointing to the same paths, must be used. Due to these options, prepending the maven goal `clean` before your normal build step typically `test` is recommended to as closely emulate the CI fresh workspace for each build.
- `pit_diff` curently only supports maven builds
- The following jars are hard coded for use by sputnik_pit and must be placed in `/opt/sputnik/pit_diff_tool/lib` (this can be changed on line 22 of `sputnik_pit.py` in `pit_diff_tool`): `junit-4.11.jar, pitest-command-line-1.1.11.jar, pitest-1.1.11.jar `. They can all be found on maven central.

## Installation

- clone this repository and build it: `gradle distZip` or download [distribution](https://github.com/TouK/sputnik/releases/download/sputnik-1.5.1/sputnik-1.5.1.zip)
- copy distribution file `build/distributions/sputnik-1.5.1.zip` to your installation dir, e.g. `/opt/sputnik` and unzip it
- to avoid problems with deployment keep the structure unchanged, so sputnik file is in `bin/` directory, jars in `lib/`
- create configuration file (you can just paste and edit an example above), e.g. `/opt/sputnik/myconf.properties`
- you can now run sputnik like this:
```
/opt/sputnik/bin/sputnik --conf /opt/sputnik/gerrit.properties --changeId I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 --revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```

### Build tool

Sputnik is intended to run just after your Jenkins/CI server build. It should be executed in the root directory of the analyzed project to find files to analyze.

Sputnik currently supports Maven (default) and Gradle. Some processors (e.g. FindBugs) analyze compiled classes, so it's important to set
the build tool property correctly. To change it to Gradle just set `project.build.tool=gradle` in your `sputnik.properties` file.

### Gerrit support

Three parameters are required: your configuration file (details below), Gerrit's changeId and revisionId:

```
sputnik --conf /path/to/conf.properties --changeId I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 --revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```

There's a bug/feature in Gerrit when you push same change set identified by particular changeId into two branches, for example your working branch and review branch.
You can recognize it by the following error message

```
Request not successful. Message: Not Found. Status-Code: 404. Content: Not found: yours_change_id
```

Then it's necessary to add repository and target branch name to distinguish which change set you're addressing. Target branch would be typically master but actually it's the one
 you're using making
```
git push origin HEAD:refs/for/BRANCH_NAME
```
So add REPO_NAME~BRANCH_NAME~ to changeId Sputnik's param, for example:

```
sputnik --conf /path/to/conf.properties --changeId myProject~master~I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0 --revisionId 3f37692af2290e8e3fd16d2f43701c24346197f0
```


### Stash support

If you choose to run sputnik with Stash instead of Gerrit, you'll need to run it in the following manner:

```
/opt/sputnik/bin/sputnik --conf /opt/sputnik/stash.properties --pullRequestId 15
```

It is convenient to add sputnik's configuration file (`myconf.properties` in the above example) to your
project's repo. This way, it will be easier to run it from CI server.

### SSL verification
SSL trust verification and hostname verification is disabled by default. You can enable it by setting `connector.verifySsl=true` property. 

### Add Post-Build step to Jenkins/CI server

If you have Jenkins job that uses Gerrit Trigger plugin it's very easy to integrate it as Post-Build step:

- create a user in Gerrit with HTTP password access and Non-Interactive Users group (take a look at Gerrit documentation [https://git.eclipse.org/r/Documentation/cmd-create-account.html](here))
- add Post-Build step to your Jenkins job: Execute bash shell:
```
/opt/sputnik/bin/sputnik --conf /opt/sputnik/myconf.properties --changeId $GERRIT_CHANGE_ID --revisionId $GERRIT_PATCHSET_REVISION
# This line makes sure that this Post-Build step always returns exit code 0
# so it won't affect your main build result
echo "exit 0 workaround"
```

### Add Post-Build step to Bamboo

When stash is build on Bamboo there is no direct way to check which pull
request id it matches. This is a simple way to find required id.

Assumptions:
- there is sputnik's config file named `sputnik.properties` in project's root directory
- user and password are configured in bamboo plan as variables (e.g.
  _ecosystem.username_ and _ecosystem.password_)
- config file has placeholders for user and password:
```properties
stash.username=<username>
stash.password=<password>
```

With those steps in place you can use a step from
`contrib/stash-execute.sh`:

```
current_branch=${bamboo.repository.branch.name} sputnik_distribution_url=https://github.com/TouK/sputnik/releases/download/sputnik-1.4.0/sputnik-1.4.0.zip stash_password=${bamboo_ecosystem_password} stash_user=${bamboo_ecosystem_username} ./stash-execute.sh
```

## Launching with Maven

If you prefer running Sputnik from Maven, there is a plugin developed by Karol Lassak here: https://github.com/ingwarsw/sputnik-maven-plugin. Read plugin documentation for reference.

## Launching with Gradle

If you prefer running Sputnik from Gradle all you need is to have Gradle installed.
Put build.gradle file in your repository, add config file and run:
```
gradle run -Dexec.args="--conf example.properties --changeId 1234 --revisionId 4321"
```

## Requirements

- Gerrit 2.8 is required (REST API for reviews was introduced in this version)
- Jenkins or other CI server to download and build patchsets

## Contributors

- Tomasz Kalkosiński
- Marcin Cylke
- Piotr Jagielski
- Karol Lassak
- Henning Hoefer
- Dominik Przybysz
- Damian Szczepanik
- Rafał Nowak
- Filip Majewski

## License

This project is licenced under Apache License.

