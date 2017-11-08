# ketos-ui-corpussummary

## About

Write about your plugin here...

## Development

### Before you start

If you are development version of `vessel-plugin`, that is you have a local clone of the repostiory you are building against, then you will want to either [link](https://yarnpkg.com/lang/en/docs/cli/link/) that so this package uses that link.

*Currently, as `vessel-plugin` is not within any public NPM registry this is the only way to develop a plugin*

In order to do this goto the `vessel-plugin` directory and run:

```
yarn link
```

then go to the `src/main/js` directory in this project and run:

```
yarn link vessel-plugin
```

### Yarn operations for development

Under `src/main/js` is a  Yarn project using Typescript and create-react-app. It is preconfigured dependencies for `vessel-plugin` as well as development support for [Storybook](https://storybook.js.org/).

Our suggested approach for development is simply to ignore the Maven build system, and work within the `src/main/js` directory as you would for any other yarn application.

Start by installing the dependencies with

```
yarn install
```

Other useful commands include:

* `yarn start` to run a hot reloading development server
* `yarn storybook`to run hot reloading storybook environment
* `yarn test` to run any tests you have, including storybook snapshots
* `yarn test -u` to update any Jest and Storybook snapshots
* `yarn build` to build a produciton version of the plugin

NOTE the default port for the development server is *3001* not 3000.

## Maven operations for packaging

You will need to be online in order to use this plugin, which will automatically download and install Yarn and NodeJS (locally to this plugin) and the dependencies of your project. If you are not online, or want to use

To run any Java and Javascript tests:

```
maven test
```

To build the plugin run:

```
maven package
```

Note this will be default run `yarn build` so you don't need to do that in advance.

### How this was created

This project was created from a Vessel Maven archetype. This is turn is based on the the [Typescript create-react-app starter](https://github.com/Microsoft/TypeScript-React-Starter). We the [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin) to integrate the Maven and Yarn processes.