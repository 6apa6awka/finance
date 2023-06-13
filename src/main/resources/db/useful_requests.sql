SELECT YEAR(a.transaction_date), MONTHNAME(a.transaction_date), SUM(a.credit), SUM(a.debit) FROM (
SELECT DATE_ADD(STR_TO_DATE('January 01 1970', '%M %d %Y'), INTERVAL t.transaction_date DAY) transaction_date,
IF(t.type = 0, t.amount, 0) credit,
IF(t.type = 1, t.amount, 0) debit
from transaction t
join service_provider sp on t.service_provider_id = sp.id) a
group by YEAR(a.transaction_date), MONTH(a.transaction_date), MONTHNAME(a.transaction_date)
ORDER BY YEAR(a.transaction_date), MONTH(a.transaction_date);
