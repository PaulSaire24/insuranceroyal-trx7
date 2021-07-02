# ![Logo-template](images/logo-template.png)
# Library PISDR020

> El objetivo de este documento es proveer información relacionada a la librería PISDR020 que utiliza la transacción PISDT007 y que ha sido implementado en APX.

### 1. Funcionalidad:

> Esta Librería APX tiene como objetivo consumir servicios externos (Rimac).

#### 1.1 Caso de Uso:

> EL uso de la Librería RBVDR020 está orientado a consumir el servicio de generación coronograma de pagos y
>  y el servicio calculo de cuotas, ambos de Rimac.

### 2. Capacidades:
> Esta **librería** brinda la capacidad de poder consumir los servicios mencionados de forma segura y fácil mediante los siguientes métodos:

#### 2.1 Método 1: executeQuoteSchedule (FinancingPlanBO input, String traceId)
> Método para obtener el calendario de cuotas que genera Rimac

##### 2.1.1 Datos de Entrada

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| FinancingPlanBO | Object | Objeto que envuelve todos los datos necesarios para el cuerpo de solicitud |
|1.1| payload | Object | Objeto que contiene datos de los planes de financiamiento |
|1.1.1| cotizacion | String | Identificador de cotización BBVA |
|1.1.2| fechaInicioFinanciamiento | LocalDate | Fecha de inicio de financiamiento |
|1.1.3| financiamiento | List | Lista que contiene los datos para la generación de financiamiento |
|1.1.3.1| financiamiento | String | Id de financiamiento |
|1.1.3.2| periodo | String | Perido de pago para el plan |
|1.1.3.3| nroCuotas | Long | Número de cuotas|

##### 2.1.2 Datos de Salida

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| FinancingPlanBO | Object | Objeto que contiene los datos de la respuesta del servicio |
|1.1| payload | Object | Objeto que contiene datos de los planes de financiamiento y sus cuotas |
|1.1.1| fechaInicio | LocalDate | Fecha de inicio de financiamiento |
|1.1.2| fechaFin | LocalDate | Fecha de fin de financiamiento |
|1.1.3| financiamiento | List | Lista que contiene los datos del financiamiento |
|1.1.3.1| financiamiento | String | Id de financiamiento |
|1.1.3.2| nroCuotas | Long | Número de cuotas|
|1.1.3.3| montoCuota | BigDecimal | Valor del monto de cuota |
|1.1.3.4| moneda | String | Moneda del monto de cuota|
|1.1.3.5| descripcionPeriodo | String | Descripción del periodo en el que se pagará el plan |
|1.1.3.6| numeroCuotasTotales | Long | Número de cuotas totales |

##### 2.1.3 Ejemplo
```java
FinancingPlanBO output = pisdR020.executeQuoteSchedule (FinancingPlanBO input, String traceId);
```

#### 2.2 Método 2: executePaymentSchedule(FinancingPlanBO input, String quotationId, String traceId)
> Método para obtener el cronograma de pago generado por Rimac

##### 2.2.1 Datos de Entrada

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| FinancingPlanBO | Object | Objeto que envuelve todos los datos necesarios para el cuerpo de solicitud |
|1.1| payload | Object | Objeto que contiene datos de los planes de financiamiento |
|1.1.1| financiamiento | List | Lista que contiene los datos del financiamiento |
|1.1.1.1| frecuencia | String | Frecuencia de pago de cuotas |
|1.1.1.2| numeroCuotas | Long | Número de cuotas |
|1.1.1.3| fechaInicio | LocalDate | Fecha de inicio del financiamiento |

##### 2.2.2 Datos de Salida

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| CronogramaPagoBO | Object | Objeto que contiene los datos de la respuesta del servicio |
|1.1| payload | Object | Objeto que contiene datos de los planes de financiamiento |
|1.1.1| fechaInicio | Date | Fecha de inicio del finaciamiento |
|1.1.2| fechaFinal | Date | Fecha de fin del financiamiento |
|1.1.3| cuotasFinanciamiento | List | Lista que contiene los datos de las cuotas de financiamiento |

##### 2.1.3 Ejemplo
```java
CronogramaPagoBO output = pisdR020.executePaymentSchedule(FinancingPlanBO input, String quotationId, String traceId);
```

### 3.  Mensajes:
#### 3.1  Código PISD00120027:
> Este código de error es devuelto cuando se presenta un error al momento de consumir el servicio calculo de cuotas del Rimac.

#### 3.2  Código PISD00120031:
> Este código de error es devuelto cuando se presenta un error al momento de consumir servicio calculo de cuotas del Rimac.

### 4.  Versiones:
#### 4.1  Versión 0.5.0-SNAPSHOT

+ Versión 0.5.0-SNAPSHOT: Esta versión permite consumir los servicios mencionados, 2 externos de Rimac.




