# Tilt

custom_build(
  'duck1123/dinsro:2-dev-sources-latest',
  'earthly +dev-image-sources',
  [
    'Earthfile',
    '.dockerignore',
    'Makefile'
  ],
  tag='2-dev-sources-latest'
)

docker_compose("./docker-compose.yml")

local_resource(
    'kondo',
    cmd='make lint-kondo',
    deps=['src'],
    allow_parallel=True
)

local_resource(
    'check',
    cmd='make check',
    deps=['src'],
    allow_parallel=True
)
