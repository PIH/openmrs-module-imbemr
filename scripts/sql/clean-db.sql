
-- Metadata

update active_list_type set creator = 1, retired_by = 1;
update concept set creator = 1, retired_by = 1, changed_by = 1;
update concept_answer set creator = 1;
update concept_class set creator = 1, retired_by = 1;
update concept_datatype set creator = 1, retired_by = 1;
update concept_description set creator = 1, changed_by = 1;
update concept_map_type set creator = 1, retired_by = 1, changed_by = 1;
update concept_name set creator = 1, voided_by = 1;
update concept_name_tag set creator = 1, voided_by = 1;
update concept_reference_map set creator = 1, changed_by = 1;
update concept_reference_source set creator = 1, retired_by = 1;
update concept_reference_term set creator = 1, retired_by = 1, changed_by = 1;
update concept_reference_term_map set creator = 1, changed_by = 1;
update concept_set set creator = 1;
update drug set creator = 1, retired_by = 1, changed_by = 1;
update encounter_role set creator = 1, retired_by = 1, changed_by = 1;
update encounter_type set creator = 1, retired_by = 1;
update field set creator = 1, retired_by = 1, changed_by = 1;
update field_answer set creator = 1;
update field_type set creator = 1;
update form set creator = 1, retired_by = 1, changed_by = 1;
update form_field set creator = 1, changed_by = 1;
update form2program_map set creator = 1, changed_by = 1;
update groovy_scripts set creator = 1, changed_by = 1;
update htmlformentry_html_form set creator = 1, retired_by = 1, changed_by = 1;
update idgen_identifier_source set creator = 1, retired_by = 1, changed_by = 1;
update location set creator = 1, retired_by = 1, changed_by = 1;
update location_attribute set creator = 1, voided_by = 1, changed_by = 1;
update location_attribute_type set creator = 1, retired_by = 1, changed_by = 1;
update location_tag set creator = 1, retired_by = 1, changed_by = 1;
update logic_rule_definition set creator = 1, retired_by = 1, changed_by = 1;
update logic_rule_token set creator = 1, changed_by = 1;
update logic_token_registration set creator = 1, changed_by = 1;
update moh_appointment_service set creator = 1, retired_by = 1;
update moh_bill_third_party set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_third_party set voided_by = 1, voided_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where voided_by is not null;
update moh_bill_insurance set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_insurance set voided_by = 1, voided_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where voided_by is not null;
update moh_bill_insurance_rate set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_insurance_rate set retired_by = 1, retired_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where retired_by is not null;
update moh_bill_billable_service set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_billable_service set retired_by = 1, retired_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where retired_by is not null;
update moh_bill_service_category set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_service_category set retired_by = 1, retired_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where retired_by is not null;
update moh_bill_facility_service_price set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_facility_service_price set retired_by = 1, retired_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where retired_by is not null;
update moh_bill_department set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_department set voided_by = 1, voided_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where voided_by is not null;
update moh_bill_hop_service set creator = 1, creator_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2';
update moh_bill_hop_service set voided_by = 1, voided_by_uuid = 'd5415c4e-2700-102b-80cb-0017a47871b2' where voided_by is not null;
update order_type set creator = 1, retired_by = 1;
update orderextension_order_set set creator = 1, retired_by = 1, changed_by = 1;
update patient_identifier_type set creator = 1, retired_by = 1;
update patientflags_displaypoint set creator = 1, retired_by = 1, changed_by = 1;
update patientflags_flag set creator = 1, retired_by = 1, changed_by = 1;
update patientflags_priority set creator = 1, retired_by = 1, changed_by = 1;
update patientflags_tag set creator = 1, retired_by = 1, changed_by = 1;
update person_attribute_type set creator = 1, retired_by = 1, changed_by = 1;
update program set creator = 1, changed_by = 1;
update program_workflow set creator = 1, changed_by = 1;
update program_workflow_state set creator = 1, changed_by = 1;
update provider_attribute_type set creator = 1, retired_by = 1, changed_by = 1;
update providermanagement_provider_role set creator = 1, retired_by = 1, changed_by = 1;
update providermanagement_provider_suggestion set creator = 1, retired_by = 1, changed_by = 1;
update providermanagement_supervision_suggestion set creator = 1, retired_by = 1, changed_by = 1;
update relationship_type set creator = 1, retired_by = 1;
update report_object set creator = 1, voided_by = 1, changed_by = 1;
update reporting_report_design set creator = 1, retired_by = 1, changed_by = 1;
update reporting_report_design_resource set creator = 1, retired_by = 1, changed_by = 1;
update reporting_report_processor set creator = 1, retired_by = 1, changed_by = 1;
update scheduler_task_config set created_by = 1, changed_by = 1;
update serialized_object set creator = 1, retired_by = 1, changed_by = 1;
update uiframework_user_defined_page_view set creator = 1;
update visit_attribute_type set creator = 1, retired_by = 1, changed_by = 1;
update visit_type set creator = 1, retired_by = 1, changed_by = 1;

-- Data

delete from active_list_allergy ;
delete from active_list_problem ;
delete from active_list ;

delete from cohort_member ;
delete from cohort ;

delete from concept_proposal_tag_map ;
delete from concept_proposal ;

delete from formentry_archive;
delete from formentry_error;
delete from formentry_queue;
delete from formentry_xsn;
delete from hl7_in_archive ;
delete from hl7_in_error ;
delete from hl7_in_queue ;
delete from hl7_source ;

delete from reporting_report_request ;

delete from dataquality_tally ;

delete from emrmonitor_report_metric ;
delete from emrmonitor_report ;
delete from emrmonitor_server ;

delete from mulindi_temp_uuid ;

delete from usagestatistics_daily ;
delete from usagestatistics_encounter ;
delete from usagestatistics_usage ;

delete from moh_appointment_changed_appointment ;
delete from moh_appointment ;

delete from moh_bill_recovery ;
delete from moh_bill_deposit_payment ;
delete from moh_bill_cash_payment ;
delete from moh_bill_paid_service_bill_refund ;
delete from moh_bill_payment_refund ;
delete from moh_bill_transaction ;
delete from moh_bill_paid_service_bill ;
delete from moh_bill_patient_service_bill ;
delete from moh_bill_consommation ;
delete from moh_bill_payment ;
delete from moh_bill_patient_bill ;
delete from moh_bill_global_bill ;
delete from moh_bill_admission;
delete from moh_bill_beneficiary;
delete from moh_bill_insurance_policy ;
delete from moh_bill_third_party_bill ;
delete from moh_bill_insurance_bill ;
delete from moh_bill_patient_account ;

delete from pharmacymanagement_arv_return_store ;
delete from pharmacymanagement_pharmacy_inventory ;
delete from pharmacymanagement_consumable_dispense ;
delete from pharmacymanagement_drug_order_prescription ;
delete from pharmacymanagement_drugproduct_inventory ;
delete from pharmacymanagement_drug_product ;
delete from pharmacymanagement_cmd_drug ;

delete from sync_server_record ;
delete from sync_server_class ;
delete from sync_import ;
delete from sync_record ;
delete from sync_server ;
delete from sync_class ;

update obs set previous_version = null;
update obs set obs_group_id = null;
delete from obs;

delete from orderextension_order ;
delete from orderextension_order_group ;

delete from drug_order ;
delete from test_order ;
delete from orders ;

delete from note ;

delete from encounter_provider ;
delete from encounter ;

delete from visit_attribute ;
delete from visit ;

delete from patient_state ;
delete from patient_program ;

delete from patient_identifier ;
delete from idgen_log_entry ;
delete from idgen_pooled_identifier ;

delete from person_address ;
delete from person_attribute ;
delete from relationship ;
delete from person_merge_log ;

delete from name_phonetics ;

delete from patient ;

delete from notification_alert_recipient ;
delete from notification_alert ;
delete from user_property ;

update idgen_remote_source set url = null, user = null, password = null;

delete from provider_attribute;

update person set creator = 1, changed_by = 1, voided_by = 1;
update person_name set creator = 1, changed_by = 1, voided_by = 1;
update provider set creator = 1, changed_by = 1, retired_by = 1;
update users set creator = 1, changed_by = 1, retired_by = 1;

delete from user_role where user_id in (select user_id from users where username not in ('admin','daemon'));
delete from users where username not in ('admin','daemon');
delete from provider where person_id not in (select person_id from users);
delete from person_name where person_id not in (select person_id from users);
delete from person where person_id not in (select person_id from users);

update global_property set property_value = null where property in (
    'mail.password', 'smtp_username', 'smtp_password', 'scheduler.username', 'scheduler.password', 'pihmalawi.excelPassword'
);

update users set password = '48bfd7625b3f7fa101298f42304f625599e2db0b3d87e46b9cda126c6f37e789fd8aa785f779dd994bb21d95d572023e7ffe3cc21090e6003af07d3733464269', salt = 'a7f1b301018944b4c6d48d87b864f32fd5a5b2759261e0901128240d93fd97dbc9a9b758699b7358270b3f646c75aac2c29d61e98c9a491c669e0966eeed5ef1' where username = 'admin';