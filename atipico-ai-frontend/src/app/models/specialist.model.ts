export interface Specialist {
  id?: number;
  name: string;
  category: string;
  contactInfo: string;
}

export interface SpecialistSummary {
  specialistName: string;
  specialty: string;
  documentTitle: string;
  documentType: string;
  aiSummary: string;
  documentDate: string;
}

