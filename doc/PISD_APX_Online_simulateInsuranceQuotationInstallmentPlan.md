# ![Logo-template](images/logo-template.png)
# Recurso APX Online insuranceroyal-trx7

> El objetivo de este documento es proveer información relacionada del API "Insurances" que utiliza este proyecto y que ha sido implementado en APX y desplegado en la consola de la plataforma.


### 1. API

> Datos del API de Catalogo implementado en el runtime de APX Online.

- API a implementar: [Insurances](https://catalogs.platform.bbva.com/apicatalog/business/apis/apis-insurances-insurances/versions/global-1.23.0/resources/insurancesapiquotationsquotationidinstallmentsplanssimulatev1/)
- SN del servicio: *(SNPE20200140)*

### 2. Servicio:

> En este apartado se detallan los endpoints implementados del API Insurances.

- simulateVehicleInsurancePlans
    - SMC del Servicio: [Documento](https://docs.google.com/spreadsheets/d/1-HfiN65vmcMU6GOtIbQv45XQH-hVpTYx4dg-bZBHOu0/edit#gid=1587295576)
    - Método HTTP: POST
    - Versión: 1.12.0
    - Endpoint: /insurances/v1/quotations/{quotation-id}/installments-plans/simulate
    - TX: [PISDT007](#PISDT007)
  
### 3. DTOs:

> En este apartado se detallan todas las clases DTOs utilizadas en este recurso.

- **PISDC011**:
  - amazon:
    - **SignatureAWS**: Entidad SignatureAWS
  - bo:
    - **FinanciamientoPayloadBO**: 
    - financing:
      - **FinancingPlanBO**: Entidad FinancingPlan - Rimac
      - **CronogramaPagoBO**: Entidad CronogramaPago - Rimac
      - **FinanciamientoPayloadBO**: Entidad FinanciamientoPayload para payload a Rimac
  - utils:
    - **PISDErrors**: Entidad Errores
    - **PISDValidation**: Entidad Validación
    - **PISDProperties**: Entidad Propiedades
  - commons:
    - **PaymentPeriodDTO**: Entidad periodo de pago
    - **InstallmentsDTO**: Entidad pago
  - financing:
    - **FinancingPlanDTO**: Entidad plan de financiamiento
  - policy:
    - **PaymentAmountDTO**: Entidad pago de cuota
  - quotdetail:
    - **QuotDetailDAO**: Entidad QuotDetai para BD
  
  
### 4. Transacciones APX:
> En este apartado se detallan todas las transacciones creadas para soportar las operaciones del servicio implementado.
- Usuario transaccional: ZG13001
- **TX - PISDT007**: Obtiene los planes de financiamiento para el servicio simulateInsuranceQuotationInstallmentPlan de Insurances
  - Código de respuesta: Http Code: 200, Severity: OK
  - Código de Respuesta: Severity: ENR
  
### 5. Librerías internas:
> En este apartado se detallan las librerías internas creadas para implementar la lógica de negocio del servicio.

- **PISDR020**: [Ver documentación](PISD_APX_Library_PISDR020.md)
- **PISDR030**: [Ver documentación](PISD_APX_Library_PISDR030.md)

### 6. Librerías externas:
> En este apartado se detallan las librerías externas que hace uso esta aplicación.

- **PISDR012**: [Ver documentación](https://globaldevtools.bbva.com/bitbucket/projects/PE_PISD_APP-ID-26197_DSG/repos/insuranceroyal-lib12/browse/doc/PISD_APX_Library_PISDR012.md?at=refs%2Fheads%2Ffeature%2Fxp61540)
  - Método reutilizado: executeRegisterAdditionalCompanyQuotaId(String companyQuotaId).
- **PISDR014**: [Ver documentación](https://globaldevtools.bbva.com/bitbucket/projects/PE_PISD_APP-ID-26197_DSG/repos/insuranceroyal-lib14/browse/doc/PISD_APX_Library_PISDR014.md?at=refs%2Fheads%2Ffeature%2Fxp61540)
  - Métodos reutilizados: executeSignatureConstruction(String payload, String httpMethod, String uri, String queryParams, String traceId)

### 7. Mensajes de Error y Avisos:
> En este apartado se detallan los distintos mensajes de error que retornan las librerías de acuerdo a los casos y lógica de negocio implementada ...

- **Advise PISD00120026**: NO SE ENCONTRÓ NINGÚN RESULTADO EN LA BD 
- **Advise PISD00120027**: ERROR AL CONSUMIR EL SERVICIO DE CÁLCULO DE CUOTA DE RIMAC
- **Advise PISD00120028**: NO SE PUEDE OBTENER PLAN DE FINANCIAMIENTO - FECHA DE INICIO DE CRONOGRAMA MENOR A LA FECHA ACTUAL
- **Advise PISD00120031**: ERROR AL CONSUMIR EL SERVICIO DE CRONOGRAMA DE PAGO DE RIMAC
- **Advise PISD00120035**: ERROR EN PETICIÓN DE FINANCIAMIENTO - CAMPO PERIODO CON VALOR INVÁLIDO

### 8. Diseño de componentes:
# ![simulateVehicleInsurancePlans](images/diseño-componentes-apx-obtener-financiamiento.png)