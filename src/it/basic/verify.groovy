def generated = new File(basedir, 'target/generated-sources/graphql/org/example/generated')
assert new File(generated, 'AppTypes.java').isFile()
assert new File(generated, 'AppDataFetchers.java').isFile()
assert new File(basedir, 'target/classes/org/example/app/StopFetchers.class').isFile()
