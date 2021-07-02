# ![Logo-template](images/logo-template.png)
# Library PISDR030

> El objetivo de este documento es proveer información relacionada a la librería PISDR030 que utiliza la transacción PISDT007 y que ha sido implementado en APX.

### 1. Funcionalidad:

> Esta Librería APX tiene como objetivo realizar la lógica de negocio de la transacción PISDT007.

#### 1.1 Caso de Uso:

> El uso de la Librería PISDR030 está orientado a realizar los mapeos de los campos de salida de la transacción, realizar validaciones, todo lo necesario para cumplir con la lógica de negocio.

### 2. Capacidades:

> Esta **librería** brinda la capacidad de poder ejecutar la lógica de negocio de la transacción de obtención de plan de financiamiento (PISDT007) de forma fácil y segura con el siguiente método:

#### 2.1 Método 1: FinancingPlanDTO executeSimulateInsuranceQuotationInstallmentPlan (FinancingPlanDTO input)
> Método que ejecuta toda la lógica de negocio

##### 2.1.1 Datos de Entrada

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| FinancingPlanDTO | Object | Objeto que envuelve los datos del cuerpo de solicitud |
|1.1| startDate | LocalDate | Fecha de inicio del financiamiento |
|1.2| creationUser | String | Parámetro header creationUser |
|1.3| userAudit | String | Parámetro header userAudit |
|1.4| saleChannelId | String | Parámetro header saleChannelId |
|1.5| traceId | String | Parámetro header traceId |
|1.6| installmentPlans | List | Lista de plan de pagos |
|1.6.1| PaymentPeriodDTO | Object | Objeto que tiene información del periódo de pagos |
|1.6.1.1| id | String | Id del período en que se pagará el plan |

##### 2.1.2 Datos de Salida

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| FinancingPlanDTO | Object | Objeto que contiene la respuesta de la transacción |
|1.1| startDate | LocalDate | Fecha de inicio del financiamiento |
|1.2| maturityDate | LocalDate | Fecha de fin del financiamiento |
|1.3| installmentPlans | List | Lista de plan de pagos|
|1.3.1| totalNumberInstallments | Long | Número de cuotas totales |
|1.3.2| PaymentPeriodDTO | Object | Objeto que tiene información del periódo de pagos |
|1.3.2.1| id | String | Id del período en que se pagará el plan |
|1.3.2.2| name | String | Descripción del período en que se pagará el plan |
|1.3.3| PaymentAmountDTO | Object | Objeto que tiene información de pago de cuotas |
|1.3.3.1| amount | Double | Valor del monto de Cuota |
|1.3.3.2| currency | String | Moneda del monto de la cuota |


##### 2.1.3 Ejemplo
```java
FinancingPlanDTO response = pisdR030.executeSimulateInsuranceQuotationInstallmentPlan (FinancingPlanDTO input);
```

### 3.  Mensajes:

#### 3.1  Código PISD00120028:
> Este código de error es devuelto cuando La fecha de inicio de cronograma es menor a la fecha actual.
> 
#### 3.2  Código PISD00120027:
> Este código de error es devuelto cuando se presenta un error al momento de consumir el servicio calculo de cuotas del Rimac.

#### 3.3  Código PISD00120031:
> Este código de error es devuelto cuando se presenta un error al momento de consumir servicio calculo de cuotas del Rimac.

#### 3.3  Código PISD00120026:
> Este código de error es devuelto cuando el código de cotización ingresado no existe en la BD.

#### 3.3  Código PISD00120035:
> Este código de error es devuelto cuando el campo periodo debe tener un valor valido.

### 4.  Versiones:
#### 4.1  Versión 0.5.0-SNAPSHOT

+ Versión 0.5.0-SNAPSHOT: Esta versión permite realizar la lógica de negocio para cumplir con el proceso deseado de la transaccion PISDT007.







