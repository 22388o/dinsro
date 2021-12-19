# specter-desktop

![Version: 0.1.0](https://img.shields.io/badge/Version-0.1.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: v0.10.4](https://img.shields.io/badge/AppVersion-v0.10.4-informational?style=flat-square)

A Helm chart for Kubernetes

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` |  |
| autoscaling.enabled | bool | `false` |  |
| autoscaling.maxReplicas | int | `100` |  |
| autoscaling.minReplicas | int | `1` |  |
| autoscaling.targetCPUUtilizationPercentage | int | `80` |  |
| debug | bool | `false` |  |
| fullnameOverride | string | `""` |  |
| image.pullPolicy | string | `"IfNotPresent"` |  |
| image.repository | string | `"lncm/specter-desktop"` |  |
| image.tag | string | `""` |  |
| imagePullSecrets | list | `[]` |  |
| ingress.annotations."cert-manager.io/cluster-issuer" | string | `"letsencrypt-staging"` |  |
| ingress.annotations."kubernetes.io/ingress.class" | string | `"traefik-cert-manager"` |  |
| ingress.annotations."traefik.ingress.kubernetes.io/frontend-entry-points" | string | `"https"` |  |
| ingress.annotations."traefik.ingress.kubernetes.io/redirect-entry-point" | string | `"https"` |  |
| ingress.annotations."traefik.ingress.kubernetes.io/rule-type" | string | `"PathPrefixStrip"` |  |
| ingress.enabled | bool | `true` |  |
| ingress.hosts[0].host | string | `"specter.apps.lsd.capital"` |  |
| ingress.hosts[0].paths[0] | string | `"/"` |  |
| ingress.tls | list | `[]` |  |
| nameOverride | string | `""` |  |
| nodeSelector | object | `{}` |  |
| persistence.enabled | bool | `true` |  |
| persistence.size | string | `"1Gi"` |  |
| podAnnotations | object | `{}` |  |
| podSecurityContext.fsGroup | int | `1000` |  |
| replicaCount | int | `1` |  |
| resources | object | `{}` |  |
| securityContext.runAsUser | int | `1000` |  |
| service.port | int | `25441` |  |
| service.type | string | `"ClusterIP"` |  |
| serviceAccount.annotations | object | `{}` |  |
| serviceAccount.create | bool | `true` |  |
| serviceAccount.name | string | `""` |  |
| tolerations | list | `[]` |  |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.4.0](https://github.com/norwoodj/helm-docs/releases/v1.4.0)